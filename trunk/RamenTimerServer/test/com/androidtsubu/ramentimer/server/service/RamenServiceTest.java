package com.androidtsubu.ramentimer.server.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Locale;

import org.junit.Test;
import org.slim3.controller.ControllerConstants;
import org.slim3.controller.upload.FileItem;
import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

import com.androidtsubu.ramentimer.server.model.Ramen;
import com.androidtsubu.ramentimer.server.service.RamenService;
import com.androidtsubu.ramentimer.server.tester.AppEngineTestCaseEx;
import com.androidtsubu.ramentimer.server.util.TestImage;

public class RamenServiceTest extends AppEngineTestCaseEx {

    private RamenService service = new RamenService();

    private String name = "ペヤングソースやきそば";
    private String jan = "4902885000686";
    private int boilTime = 180;
    private long twitterId = 14070046;
    private byte[] imageData = TestImage.load("sample.jpg");
    private byte[] resizedImageData = TestImage.load("sample_resized.jpg");

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }

    @Test
    public void create() throws Exception {
        ApplicationMessage.setBundle(
            ControllerConstants.DEFAULT_LOCALIZATION_CONTEXT,
            new Locale("en"));

        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("name", name);
        input.put("jan", jan);
        input.put("boilTime", ""+boilTime); // なぜかintをそのまま渡すとValidators.integerType()が通らないので文字列に...
        input.put("twitterId", ""+twitterId);
        input.put("image", new FileItem("sample.jpg", "image/jpeg", imageData));

        tester.imageServiceStub.register("Transform", resizedImageData);
        Ramen ramen = service.create(input);
        assertThat(ramen, is(notNullValue()));

        Ramen got = Datastore.get(Ramen.class, ramen.getKey());
        assertThat(got.getName(), is(name));
        assertThat(got.getJan(), is(jan));
        assertThat(got.getBoilTime(), is(boilTime));
        assertThat(got.getTwitterId(), is(twitterId));
        assertThat(got.getImageData(), is(resizedImageData));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createErrorNoInput() throws Exception {
        HashMap<String, Object> input = new HashMap<String, Object>();
        service.create(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createErrorExistsJANCode() throws Exception {
        putRamen();
        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("name", name);
        input.put("jan", jan);
        input.put("boilTime", ""+boilTime);
        input.put("twitterId", ""+twitterId);
        input.put("image", new FileItem("sample.jpg", "image/jpeg", imageData));
        service.create(input);
    }

    @Test
    public void findByJan() throws Exception {
        Ramen ramen = putRamen();
        Ramen got = service.findByJan(jan);
        assertThat(got, is(notNullValue()));
        assertThat(ramen.equals(got), is(true));
    }

    private Ramen putRamen() {
        Ramen ramen = new Ramen();
        ramen.setName(name);
        ramen.setJan(jan);
        ramen.setBoilTime(boilTime);
        ramen.setTwitterId(twitterId);
        ramen.setImageData(imageData);
        Datastore.put(ramen);
        return ramen;
    }
}
