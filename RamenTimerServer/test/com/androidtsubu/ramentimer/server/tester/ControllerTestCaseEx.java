package com.androidtsubu.ramentimer.server.tester;

import org.junit.After;
import org.junit.Before;

public abstract class ControllerTestCaseEx {
    
    protected ControllerTesterEx tester =
        new ControllerTesterEx(getClass());

    @Before
    public void setUp() throws Exception {
        tester.setUp();
    }

    @After
    public void tearDown() throws Exception {
        tester.tearDown();
    }
}
