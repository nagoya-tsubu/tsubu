package com.androidtsubu.ramentimer.server.util;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

public class ControllerUtil {

    static public void setErrorMessage(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        HashMap<String,String> map = new HashMap<String, String>();
        map.put("message", message);
        response.getWriter().write(JSON.encode(map));
        response.flushBuffer();
    }
}
