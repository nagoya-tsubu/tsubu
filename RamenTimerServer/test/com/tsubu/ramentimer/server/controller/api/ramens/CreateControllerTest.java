package com.tsubu.ramentimer.server.controller.api.ramens;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.slim3.controller.upload.FileItem;
import org.slim3.datastore.Datastore;

import com.tsubu.ramentimer.server.model.Ramen;
import com.tsubu.ramentimer.server.tester.ControllerTestCaseEx;
import com.tsubu.ramentimer.server.util.TestImage;

public class CreateControllerTest extends ControllerTestCaseEx {

    @Test
    public void run() throws Exception {
        String name = "ペヤングソースやきそば";
        String jan = "4902885000686";
        int boilTime = 180;
        byte[] image = TestImage.load("sample.jpg");
        byte[] resizedImage = TestImage.load("sample_resized.jpg");

        tester.imageServiceStub.register("Transform", resizedImage);
        tester.request.setMethod("post");
        tester.request.addLocale(new Locale("en"));
        tester.param("name", name);
        tester.param("jan", jan);
        tester.param("boilTime", boilTime);
        tester.requestScope("image", new FileItem("upload.jpg", "image/jpeg", image));
        
        tester.start("/api/ramens/create");
        CreateController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
        
        assertThat(tester.response.getStatus(), is(200));
        assertThat(tester.count(Ramen.class), is(1));
        Ramen stored = Datastore.query(Ramen.class).asSingle();
        assertThat(stored.getName(), is(name));
        assertThat(stored.getJan(), is(jan));
        assertThat(stored.getBoilTime(), is(boilTime));
        assertThat(stored.getImageData(), is(resizedImage));
    }
}
