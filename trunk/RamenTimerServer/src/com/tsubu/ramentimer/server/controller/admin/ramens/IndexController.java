package com.tsubu.ramentimer.server.controller.admin.ramens;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.tsubu.ramentimer.server.service.RamenService;

public class IndexController extends Controller {

    @Override
    public Navigation run() throws Exception {
        RamenService service = new RamenService();
        requestScope("ramens", service.getRamens());
        return forward("index.jsp");
    }
}
