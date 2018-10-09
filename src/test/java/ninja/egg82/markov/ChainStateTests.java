package ninja.egg82.markov;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class ChainStateTests {
    // vars

    // constructor

    // public
    @Test
    public void testNullArrayException() {
        Character[] value = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ChainState<Character>(value), "new instance of ChainState did not throw the expected exception.");
        System.out.println("Invalid array items correctly throws exceptions.");
        System.out.flush();
    }
    @Test
    public void testNullCollectionException() {
        Collection<Character> value = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ChainState<Character>(value), "new instance of ChainState did not throw the expected exception.");
        System.out.println("Invalid collection items correctly throws exceptions.");
        System.out.flush();
    }
    @Test
    public void testEqualsDifferent() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = new ChainState<>(new Character[] { 'a', 'a', 'b' });

        Assertions.assertNotEquals(a, b, "different StateChain values equal eachother!");
        System.out.println("ChainStates with differing values (correctly) do not equal eachother.");
        System.out.flush();
    }
    @Test
    public void testEqualsNullDifferent() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = null;

        Assertions.assertNotEquals(a, b, "StateChain and null equal eachother!");
        System.out.println("ChainStates with one null (correctly) do not equal eachother.");
        System.out.flush();
    }
    @Test
    public void testEqualsSameRef() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = a;

        Assertions.assertEquals(a, b, "StateChain does not equal a reference to itself!");
        System.out.println("ChainStates with same reference (correctly) equal eachother.");
        System.out.flush();
    }
    @Test
    public void testEqualsSame() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = new ChainState<>(new Character[] { 'a', 'a', 'a' });

        Assertions.assertEquals(a, b, "same StateChain values do not equal eachother!");
        System.out.println("ChainStates with same values (correctly) equal eachother.");
        System.out.flush();
    }

    // private

}
