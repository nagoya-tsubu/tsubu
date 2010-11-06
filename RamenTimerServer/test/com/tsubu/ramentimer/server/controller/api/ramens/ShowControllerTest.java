package com.tsubu.ramentimer.server.controller.api.ramens;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.tsubu.ramentimer.server.model.Ramen;
import com.tsubu.ramentimer.server.util.TestImage;

public class ShowControllerTest extends ControllerTestCase {

    private String name = "ペヤングソースやきそば";
    private String jan = "4902885000686";
    private int boilTime = 180;
    private byte[] imageData = TestImage.load("sample.jpg");
    
    @Test
    public void run() throws Exception {
        Ramen ramen = putRamen();
        
        tester.request.setMethod("get");
        tester.param("jan", jan);
        tester.start("/api/ramens/show");
        ShowController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
        
//        System.out.println(tester.response.getOutputAsString());
        Map json = JSON.decode(tester.response.getOutputAsString(), Map.class);
        assertThat(json.get("name").toString(), is(ramen.getName()));
        assertThat(json.get("jan").toString(), is(ramen.getJan()));
        assertThat(Integer.parseInt(json.get("boilTime").toString()), is(ramen.getBoilTime()));
        assertThat(json.get("imageUrl").toString(), is(ramen.getImageUrl()));
    }
    
    private Ramen putRamen() {
        Ramen ramen = new Ramen();
        ramen.setName(name);
        ramen.setJan(jan);
        ramen.setBoilTime(boilTime);
        ramen.setImageData(imageData);
        Datastore.put(ramen);
        return ramen;
    }
}
