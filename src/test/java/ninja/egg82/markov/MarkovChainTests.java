package ninja.egg82.markov;

import ninja.egg82.primitive.ints.Object2IntArrayMap;
import ninja.egg82.primitive.ints.Object2IntMap;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MarkovChainTests {
    // vars

    // constructor

    // public
    @Test
    public void testAddsValuesToState() {
        MarkovChain<Character> chain;

        chain = new MarkovChain<>(1);
        chain.add(new Character[] { 'f', 'o', 'o', 'l' });
        assertEquals(serialize(chain), "{'':{'f':1},'f':{'o':1},'l':{'':1},'o':{'o':1,'l':1}}");

        chain = new MarkovChain<>(1);
        chain.add(new Character[] { 'f', 'o', 'o', 'd' });
        assertEquals(serialize(chain), "{'':{'f':1},'d':{'':1},'f':{'o':1},'o':{'o':1,'d':1}}");

        chain = new MarkovChain<>(1);
        chain.add(new Character[] { 'l', 'o', 'o', 's', 'e' });
        assertEquals(serialize(chain), "{'':{'l':1},'s':{'e':1},'e':{'':1},'l':{'o':1},'o':{'o':1,'s':1}}");

        System.out.println("\"add\" correctly adds values to the state.");
    }
    @Test
    public void testOppositeWeightResets() {
        MarkovChain<Character> chain = new MarkovChain<>(1);

        chain.add(new Character[] { 'f', 'o', 'o', 'l' }, 1);
        chain.add(new Character[] { 'f', 'o', 'o', 'l' }, -1);

        assertEquals(serialize(chain), "{}");

        System.out.println("Opposing weights correctly reset the state.");
    }
    @Test
    public void testOrderException() {
        assertThrows(IllegalArgumentException.class, () -> new MarkovChain<Character>(-1));
        System.out.println("Invalid order correctly throws exceptions.");
    }

    // private
    private String serialize(MarkovChain<Character> chain) {
        Iterator<ChainState<Character>> iterator = chain.getStates().iterator();
        Map<String, Object2IntMap<String>> map = new HashMap<>();
        while (iterator.hasNext()) {
            ChainState<Character> i = iterator.next();

            Object2IntMap<Character> next = chain.getNextStates(i);
            if (next == null) {
                next = new Object2IntArrayMap<>();
            }
            Object2IntMap<String> result = new Object2IntArrayMap<>();
            for (Object2IntMap.Entry<Character> kvp : next.object2IntEntrySet()) {
                result.put(kvp.getKey().toString(), kvp.getIntValue());
            }

            int terminal = chain.getTerminalWeight(i);
            if (terminal > 0) {
                result.put("", terminal);
            }

            map.put(StreamSupport.stream(i.spliterator(), true)
                    .collect(
                            StringBuilder::new,
                            StringBuilder::append,
                            StringBuilder::append
                    )
                    .toString(), result);
        }

        return JSONObject.toJSONString(map).replaceAll("\"", "'");
    }
}
