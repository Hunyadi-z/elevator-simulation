package com.elevatorsimulation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DoorsTest {
    private Doors doors;

    @BeforeEach
    void init() {
        doors = new Doors();
    }

    @Test
    void testDoorsAreClosed() {
        assertEquals(true, doors.areClosed());
    }

    @Test
    void testDoorsOpen() {
        doors.open();
        assertEquals(false, doors.areClosed());
    }

    @Test
    void testDoorsClose() {
        doors.open();
        doors.close();
        assertEquals(true, doors.areClosed());
    }

    @AfterEach
    void tearDown() {
        doors = null;
    }
}
