package com.androidtsubu.ramentimer.server.controller.api.ramens;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.RequestMap;

import com.androidtsubu.ramentimer.server.service.RamenService;
import com.androidtsubu.ramentimer.server.util.ControllerUtil;

public class CreateController extends Controller {
    private static Logger logger = Logger.getLogger(CreateController.class.getName());
    
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
            logger.log(Level.WARNING, "登録に失敗しました。" + e.getMessage());
            logingParams(request);
            ControllerUtil.setErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private void logingParams(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        RequestMap map = new RequestMap(request);
        for (String key : map.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(map.get(key));
            sb.append(" ");
        }
        logger.log(Level.WARNING, "params: " + sb.toString());
    }
}
