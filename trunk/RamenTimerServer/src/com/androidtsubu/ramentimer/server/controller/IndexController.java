package com.androidtsubu.ramentimer.server.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.S3QueryResultList;

import com.androidtsubu.ramentimer.server.model.Ramen;
import com.androidtsubu.ramentimer.server.service.RamenService;

public class IndexController extends Controller {
    @Override
    public Navigation run() throws Exception {
        int limit = 20;
        RamenService service = new RamenService();
        S3QueryResultList<Ramen> results = service.recentList(limit, asString("c"));

        requestScope("ramens", results);
        requestScope("c", results.getEncodedCursor());
        requestScope("hasNext", results.hasNext());
        requestScope("count", service.count());
        return forward("index.jsp");
    }
}
