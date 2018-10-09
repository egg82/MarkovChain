package ninja.egg82.markov;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import ninja.egg82.primitive.ints.Object2IntArrayMap;
import ninja.egg82.primitive.ints.Object2IntMap;

public final class MarkovChain<E> {
    // vars
    private final ConcurrentMap<ChainState<E>, Object2IntMap<E>> items = new ConcurrentHashMap<>();
    private final int order;
    private final Object2IntMap<ChainState<E>> terminals = new Object2IntArrayMap<>();

    // constructor
    public MarkovChain(int order) {
        if (order < 0) {
            throw new IllegalArgumentException("order cannot be < 0.");
        }
        this.order = order;
    }

    // public
    public void add(E[] items) {
        add(Arrays.asList(items), 1);
    }
    public void add(E[] items, int weight) {
        add(Arrays.asList(items), weight);
    }
    public void add(E[] items, E item, int weight) {
        add(Arrays.asList(items), item, weight);
    }
    public void add(Collection<E> items) {
        add(items, 1);
    }
    public void add(Collection<E> items, int weight) {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null.");
        }

        Queue<E> previous = new ArrayDeque<>();
        for (E item : items) {
            ChainState<E> key = new ChainState<>(previous);
            add(key, item, weight);
            previous.add(item);
            if (previous.size() > order) {
                previous.remove();
            }
        }

        ChainState<E> terminalKey = new ChainState<>(previous);
        terminals.compute(terminalKey, (k, v) -> {
            int newWeight = Math.max(0, v != null ? weight + v : weight);
            return newWeight == 0 ? null : newWeight;
        });
    }
    public void add(Collection<E> previous, E item) {
        if (previous == null) {
            throw new IllegalArgumentException("previous cannot be null.");
        }

        Queue<E> state = new ArrayDeque<>(previous);
        while (state.size() > order) {
            state.remove();
        }

        add(new ChainState<>(state), item, 1);
    }
    public void add(ChainState<E> state, E next) {
        add(state, next, 1);
    }
    public void add(Collection<E> previous, E item, int weight) {
        if (previous == null) {
            throw new IllegalArgumentException("previous cannot be null.");
        }

        Queue<E> state = new ArrayDeque<>(previous);
        while (state.size() > order) {
            state.remove();
        }

        add(new ChainState<>(state), item, weight);
    }
    public void add(ChainState<E> state, E next, int weight) {
        if (state == null) {
            throw new IllegalArgumentException("state cannot be null.");
        }

        Object2IntMap<E> weights = items.computeIfAbsent(state, k -> new Object2IntArrayMap<>());

        weights.compute(next, (k, v) -> {
            int newWeight = Math.max(0, v != null ? weight + v : weight);
            if (newWeight == 0) {
                if (weights.size() == 1) {
                    items.remove(state);
                }
                return null;
            } else {
                return newWeight;
            }
        });
    }

    public Iterable<E> chain() {
        return chain(Collections.emptyList(), new Random());
    }
    public Iterable<E> chain(E[] previous) {
        return chain(Arrays.asList(previous), new Random());
    }
    public Iterable<E> chain(Collection<E> previous) {
        return chain(previous, new Random());
    }
    public Iterable<E> chain(int seed) {
        return chain(Collections.emptyList(), new Random(seed));
    }
    public Iterable<E> chain(E[] previous, int seed) {
        return chain(Arrays.asList(previous), new Random(seed));
    }
    public Iterable<E> chain(Collection<E> previous, int seed) {
        return chain(previous, new Random(seed));
    }
    public Iterable<E> chain(Random rand) {
        return chain(Collections.emptyList(), rand);
    }
    public Iterable<E> chain(E[] previous, Random rand) { return chain(Arrays.asList(previous), rand); }
    public Iterable<E> chain(Collection<E> previous, Random rand) {
        if (previous == null) {
            throw new IllegalArgumentException("previous cannot be null.");
        }
        if (rand == null) {
            throw new IllegalArgumentException("rand cannot be null.");
        }

        return () -> new ChainIterator(previous, rand);
    }

    public Object2IntMap<E> getInitialStates() {
        return getNextStates(new ChainState<>(Collections.emptyList()));
    }
    public Object2IntMap<E> getNextStates(Collection<E> previous) {
        Queue<E> state = new ArrayDeque<>(previous);
        while (state.size() > order) {
            state.remove();
        }
        return getNextStates(new ChainState<>(state));
    }
    public Object2IntMap<E> getNextStates(ChainState<E> state) {
        Object2IntMap<E> weights = items.get(state);
        if (weights != null) {
            return new Object2IntArrayMap<>(weights);
        }
        return null;
    }
    public Iterable<ChainState<E>> getStates() {
        return () -> new StatesIterator();
    }

    public int getTerminalWeight(Collection<E> previous) {
        Queue<E> state = new ArrayDeque<>(previous);
        while (state.size() > order) {
            state.remove();
        }
        return getTerminalWeight(new ChainState<>(state));
    }
    public int getTerminalWeight(ChainState<E> state) {
        return terminals.getInt(state);
    }

    // private
    class ChainIterator implements Iterator<E> {
        // vars
        private Queue<E> state;
        private E nextItem;
        private Random rand;

        private int currentWeight;
        private int value;
        private Iterator<Object2IntMap.Entry<E>> weightsIterator = null;

        // constructor
        public ChainIterator(Collection<E> previous, Random rand) {
            this.rand = rand;
            state = new ArrayDeque<>(previous);
            nextItem = getNext();
        }

        // public
        public boolean hasNext() {
            return nextItem != null;
        }
        public synchronized E next() {
            if (nextItem == null) {
                throw new NoSuchElementException();
            }

            E previousItem = nextItem;
            nextItem = getNext();
            return previousItem;
        }

        // private
        private E getNext() {
            if (weightsIterator != null) {
                return getNextInner();
            }

            while (state.size() > order) {
                state.remove();
            }

            ChainState<E> key = new ChainState<>(state);
            Object2IntMap<E> weights = items.get(key);
            if (weights == null) {
                return null;
            }

            int terminalWeight = terminals.getInt(key);
            int total = getTotal(weights);
            value = rand.nextInt(total + terminalWeight) + 1;
            if (value > total) {
                return null;
            }

            currentWeight = 0;
            weightsIterator = weights.object2IntEntrySet().iterator();
            return getNextInner();
        }
        private E getNextInner() {
            E retVal = null;

            while (weightsIterator.hasNext()) {
                Object2IntMap.Entry<E> kvp = weightsIterator.next();
                currentWeight += kvp.getIntValue();
                if (currentWeight >= value) {
                    retVal = kvp.getKey();
                    state.add(kvp.getKey());
                    weightsIterator = null;
                    break;
                }
            }
            if (weightsIterator != null && !weightsIterator.hasNext()) {
                weightsIterator = null;
            }

            return retVal;
        }

        private int getTotal(Object2IntMap<E> weights) {
            AtomicInteger total = new AtomicInteger(0);
            weights.forEach((k, v) -> total.addAndGet(v));
            return total.get();
        }
    }
    class StatesIterator implements Iterator<ChainState<E>> {
        // vars
        private ChainState<E> nextItem;
        private Iterator<ChainState<E>> itemsIterator = items.keySet().iterator();
        private Iterator<ChainState<E>> terminalsIterator = terminals.keySet().iterator();

        // constructor
        public StatesIterator() {
            nextItem = getNext();
        }

        // public
        public boolean hasNext() {
            return nextItem != null;
        }
        public synchronized ChainState<E> next() {
            if (nextItem == null) {
                throw new NoSuchElementException();
            }

            ChainState<E> previousItem = nextItem;
            nextItem = getNext();
            return previousItem;
        }

        // private
        private ChainState<E> getNext() {
            if (itemsIterator.hasNext()) {
                return itemsIterator.next();
            }
            while (terminalsIterator.hasNext()) {
                ChainState<E> retVal = terminalsIterator.next();
                if (!items.containsKey(retVal)) {
                    return retVal;
                }
            }
            return null;
        }
    }
}