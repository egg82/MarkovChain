package ninja.egg82.markov;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChainStateTests {
    // vars

    // constructor

    // public
    @Test
    public void testNullArrayException() {
        Character[] value = null;
        assertThrows(IllegalArgumentException.class, () -> new ChainState<Character>(value));
        System.out.println("Invalid array items correctly throws exceptions.");
    }
    @Test
    public void testNullCollectionException() {
        Collection<Character> value = null;
        assertThrows(IllegalArgumentException.class, () -> new ChainState<Character>(value));
        System.out.println("Invalid collection items correctly throws exceptions.");
    }
    @Test
    public void testEqualsDifferent() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = new ChainState<>(new Character[] { 'a', 'a', 'b' });

        assertNotEquals(a, b);
        System.out.println("ChainStates with differing values (correctly) do not equal eachother.");
    }
    @Test
    public void testEqualsNullDifferent() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = null;

        assertNotEquals(a, b);
        System.out.println("ChainStates with one null (correctly) do not equal eachother.");
    }
    @Test
    public void testEqualsSameRef() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = a;

        assertEquals(a, b);
        System.out.println("ChainStates with same reference (correctly) equal eachother.");
    }
    @Test
    public void testEqualsSame() {
        ChainState<Character> a = new ChainState<>(new Character[] { 'a', 'a', 'a' });
        ChainState<Character> b = new ChainState<>(new Character[] { 'a', 'a', 'a' });

        assertEquals(a, b);
        System.out.println("ChainStates with same values (correctly) equal eachother.");
    }

    // private

}
