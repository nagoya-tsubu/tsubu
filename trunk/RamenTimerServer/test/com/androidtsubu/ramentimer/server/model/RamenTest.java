package com.androidtsubu.ramentimer.server.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.androidtsubu.ramentimer.server.model.Ramen;

public class RamenTest extends AppEngineTestCase {

    private Ramen model = new Ramen();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
    
    @Test
    public void toJson() throws Exception {
//        System.out.println(model.toJson());
    }
}
