package com.tsubu.ramentimer.server.controller.api.ramens;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.tsubu.ramentimer.server.model.Ramen;

public class ImageController extends Controller {

    @Override
    public Navigation run() throws Exception {
        Ramen ramen = Datastore.get(Ramen.class, asKey("key"));
        if (ramen == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        response.setContentType("image/jpeg");
        response.getOutputStream().write(ramen.getImageData());
        response.flushBuffer();
        return null;
    }
}
