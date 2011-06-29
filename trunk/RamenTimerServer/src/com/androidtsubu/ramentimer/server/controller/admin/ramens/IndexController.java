package com.androidtsubu.ramentimer.server.controller.admin.ramens;

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
        return forward("index.jsp");
    }
}
