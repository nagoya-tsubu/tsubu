package com.androidtsubu.ramentimer.server.controller.api.ramens;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.androidtsubu.ramentimer.server.model.Ramen;

public class CountControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        List<Ramen> list = new ArrayList<Ramen>();
        list.add(new Ramen());
        list.add(new Ramen());
        list.add(new Ramen());
        Datastore.put(list);

        tester.start("/api/ramens/count");
        CountController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));

        assertThat(tester.response.getOutputAsString(), is("3"));
    }
}
