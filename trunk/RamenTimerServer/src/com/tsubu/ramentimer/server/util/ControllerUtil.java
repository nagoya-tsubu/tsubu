package com.tsubu.ramentimer.server.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ControllerUtil {

    static public void setStatusAndMessage(HttpServletResponse response, int status, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.getWriter().write(message);
        response.getWriter().flush();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
