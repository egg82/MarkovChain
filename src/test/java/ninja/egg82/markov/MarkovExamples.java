package ninja.egg82.markov;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public class MarkovExamples {
    // vars

    // constructor

    // public
    @Test
    public void testWords() {
        MarkovChain<Character> chain = new MarkovChain<>(2);

        chain.add(new Character[] { 'f', 'o', 'o', 'l' }, 1);
        chain.add(new Character[] { 'f', 'o', 'o', 'd' }, 1);
        chain.add(new Character[] { 'l', 'o', 'o', 's', 'e' }, 1);

        for (int i = 0; i < 10; i++) {
            System.out.println(StreamSupport.stream(chain.chain(
            ).spliterator(), true)
                    .collect(
                            StringBuilder::new,
                            StringBuilder::append,
                            StringBuilder::append
                    )
                    .toString());
        }
    }

    @Test
    public void testSentences() {
        MarkovChain<String> chain = new MarkovChain<>(1);

        chain.add(new String[] { "Once", "upon", "a", "time." }, 1);
        chain.add(new String[] { "Once", "there", "was", "a", "pig." }, 1);
        chain.add(new String[] { "There", "once", "was", "a", "man", "from", "Nantucket." }, 1);

        for (int i = 0; i < 10; i++) {
            System.out.println(String.join(" ", chain.chain()));
        }
    }

    @Test
    public void testMarket() {
        MarkovChain<String> chain = new MarkovChain<>(1);

        chain.add(new String[] { "Bull" }, "Bull", 900);
        chain.add(new String[] { "Bull" }, "Bear", 075);
        chain.add(new String[] { "Bull" }, "Recession", 025);
        chain.add(new String[] { "Bear" }, "Bull", 150);
        chain.add(new String[] { "Bear" }, "Bear", 800);
        chain.add(new String[] { "Bear" }, "Recession", 050);
        chain.add(new String[] { "Recession" }, "Bull", 250);
        chain.add(new String[] { "Recession" }, "Bear", 250);
        chain.add(new String[] { "Recession" }, "Recession", 500);

        Iterator<String> iterator = chain.chain(new String[] { "Bull" }).iterator();
        for (int i = 0; i < 10; i++) {
            System.out.println(iterator.next());
        }
    }

    // private

}
