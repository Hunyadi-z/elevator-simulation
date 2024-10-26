package com.elevatorsimulation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ButtonTest {

    @Test
    void testEqualsMethod() {
        Button button1 = new Button(1, Direction.NONE);
        Button button2 = new Button(1, Direction.NONE);
        Button button3 = new Button(1, Direction.NONE);
        Button button4 = new Button(1, Direction.UP);
        Button button5 = new Button(2, Direction.NONE);

        // Reflexive
        assertEquals(true, button1.equals(button1));

        // Symmetric
        assertEquals(true, button1.equals(button2));
        assertEquals(true, button2.equals(button1));
        assertEquals(false, button1.equals(button4));
        assertEquals(false, button4.equals(button1));

        // Transitive
        assertEquals(true, button2.equals(button3));
        assertEquals(true, button1.equals(button3));

        // NULL
        assertEquals(false, button1.equals(null));

        // Other Failures
        assertEquals(false, button1.equals(button5));
    }
}
