package com.androidtsubu.ramentimer.server.controller.admin.ramens;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.androidtsubu.ramentimer.server.controller.admin.ramens.NewController;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class NewControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/admin/ramens/new");
        NewController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is("/admin/ramens/new.jsp"));
    }
}
