package com.androidtsubu.ramentimer.server.controller.api.ramens;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.androidtsubu.ramentimer.server.model.Ramen;
import com.androidtsubu.ramentimer.server.service.RamenService;

public class ShowController extends Controller {

    @Override
    public Navigation run() throws Exception {
        RamenService service = new RamenService();
        Ramen ramen = service.findByJan(asString("jan"));
        if (ramen == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        String json = ramen.toJson();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(json);
        response.flushBuffer();
        return null;
    }
}
