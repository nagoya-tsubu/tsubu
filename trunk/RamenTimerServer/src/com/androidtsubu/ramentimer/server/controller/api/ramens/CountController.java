package com.androidtsubu.ramentimer.server.controller.api.ramens;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.memcache.Memcache;

import com.androidtsubu.ramentimer.server.service.RamenService;

public class CountController extends Controller {

    private static final String MEMCACHE_KEY_COUNT = "ramenCount";

    @Override
    public Navigation run() throws Exception {
        int count = 0;
        if (Memcache.contains(MEMCACHE_KEY_COUNT)) {
            count = Memcache.get(MEMCACHE_KEY_COUNT);
        } else {
            RamenService service = new RamenService();
            count = service.count();
            Memcache.put(MEMCACHE_KEY_COUNT, count);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(""+count);
        response.flushBuffer();
        return null;
    }
}
