package com.androidtsubu.ramentimer.server.tester;

import org.junit.After;
import org.junit.Before;
import org.slim3.tester.AppEngineTestCase;

public abstract class AppEngineTestCaseEx extends AppEngineTestCase {
    
    protected AppEngineTesterEx tester = new AppEngineTesterEx();
    
    @Before
    public void setUp() throws Exception {
        tester.setUp();
    }

    @After
    public void tearDown() throws Exception {
        tester.tearDown();
    }
}
