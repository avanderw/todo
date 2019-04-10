package net.avdw.todo;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void testList() {
        Main.main(new String[]{"ls"});
    }
}