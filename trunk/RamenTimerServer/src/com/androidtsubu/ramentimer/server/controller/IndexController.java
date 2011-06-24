package com.androidtsubu.ramentimer.server.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.S3QueryResultList;

import com.androidtsubu.ramentimer.server.model.Ramen;
import com.androidtsubu.ramentimer.server.service.RamenService;

public class IndexController extends Controller {
    @Override
    public Navigation run() throws Exception {
        int limit = 20;

        S3QueryResultList<Ramen> results;
        if (asString("c") == null) {
            results =
                Datastore.query(Ramen.class)
                         .limit(limit)
                         .asQueryResultList();
        } else {
            results =
                Datastore.query(Ramen.class)
                         .encodedStartCursor(asString("c"))
                         .limit(limit)
                         .asQueryResultList();
        }

        requestScope("ramens", results);
        requestScope("c", results.getEncodedCursor());
        requestScope("hasNext", results.hasNext());
        requestScope("count", new RamenService().count());
        return forward("index.jsp");
    }
}
