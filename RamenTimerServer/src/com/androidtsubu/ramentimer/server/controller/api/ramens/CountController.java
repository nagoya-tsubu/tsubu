package com.androidtsubu.ramentimer.server.controller.api.ramens;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.androidtsubu.ramentimer.server.service.RamenService;

public class CountController extends Controller {
    @Override
    public Navigation run() throws Exception {
        RamenService service = new RamenService();
        int count = service.count();

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(""+count);
        response.flushBuffer();
        return null;
    }
}
