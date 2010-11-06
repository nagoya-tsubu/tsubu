package com.androidtsubu.ramentimer.server.controller.admin.ramens;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.androidtsubu.ramentimer.server.controller.admin.ramens.IndexController;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IndexControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/admin/ramens/index");
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is("/admin/ramens/index.jsp"));
    }
}
