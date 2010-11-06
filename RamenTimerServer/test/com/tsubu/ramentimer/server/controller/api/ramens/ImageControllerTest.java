package com.tsubu.ramentimer.server.controller.api.ramens;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.google.appengine.api.datastore.KeyFactory;
import com.tsubu.ramentimer.server.model.Ramen;
import com.tsubu.ramentimer.server.util.TestImage;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ImageControllerTest extends ControllerTestCase {
    
    private byte[] imageData = TestImage.load("sample.jpg");

    @Test
    public void run() throws Exception {
        Ramen ramen = putRamen();
        
        tester.request.setMethod("get");
        tester.param("key", KeyFactory.keyToString(ramen.getKey()));
        tester.start("/api/ramens/image");
        ImageController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
        
        assertThat(tester.response.getContentType(), is("image/jpeg"));
        assertThat(tester.response.getOutputAsByteArray(), is(imageData));
    }
    
    private Ramen putRamen() {
        Ramen ramen = new Ramen();
        ramen.setImageData(imageData);
        Datastore.put(ramen);
        return ramen;
    }
}
