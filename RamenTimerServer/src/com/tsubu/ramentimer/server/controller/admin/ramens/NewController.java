package com.tsubu.ramentimer.server.controller.admin.ramens;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class NewController extends Controller {

    @Override
    public Navigation run() throws Exception {
        return forward("new.jsp");
    }
}
