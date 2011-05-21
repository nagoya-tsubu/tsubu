package com.androidtsubu.ramentimer.server.controller.api.ramens;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

public class DeleteController extends Controller {

    @Override
    public Navigation run() throws Exception {
        if (asKey("key") == null) {
            return null;
        }
        Datastore.delete(asKey("key"));
        return null;
    }
}
