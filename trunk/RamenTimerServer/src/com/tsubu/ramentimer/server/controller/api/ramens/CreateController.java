package com.tsubu.ramentimer.server.controller.api.ramens;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.tsubu.ramentimer.server.service.RamenService;
import com.tsubu.ramentimer.server.util.ControllerUtil;

public class CreateController extends Controller {

    @Override
    public Navigation run() throws Exception {
        if (isPost() == false) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        try {
            RamenService service = new RamenService();
            service.create(request);
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        } catch (IllegalArgumentException e) {
            ControllerUtil.setStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return null;
        }
    }
}
