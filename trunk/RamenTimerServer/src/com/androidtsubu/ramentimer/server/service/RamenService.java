package com.androidtsubu.ramentimer.server.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slim3.controller.ControllerConstants;
import org.slim3.controller.upload.FileItem;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;
import org.slim3.util.BeanUtil;
import org.slim3.util.RequestMap;

import com.androidtsubu.ramentimer.server.model.Ramen;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.androidtsubu.ramentimer.server.meta.RamenMeta;

public class RamenService {

    public Ramen create(HttpServletRequest request) {
        ApplicationMessage.setBundle(
            ControllerConstants.DEFAULT_LOCALIZATION_CONTEXT,
            request.getLocale());

        return create(new RequestMap(request));
    }

    public Ramen create(HashMap<String, Object> input) {
        input.put(ControllerConstants.ERRORS_KEY, new Errors());
        Validators v = new Validators(input);
        v.add("name", v.required(), v.maxlength(100));
        v.add("jan", v.required(), v.maxlength(13));
        v.add("boilTime", v.required(), v.integerType(), v.longRange(1, 300));
        v.add("image", v.required());
        if (v.validate() == false) {
            throw new IllegalArgumentException(v
                .getErrors()
                .values()
                .toString());
        }

        if (findByJan(input.get("jan").toString()) != null) {
            throw new IllegalArgumentException("JAN code already exists.");
        }

        Ramen ramen = new Ramen();
        BeanUtil.copy(input, ramen);

        FileItem item = (FileItem) input.get("image");
        ramen.setImageData(resizeImage(item.getData()));

        Datastore.put(ramen);
        return ramen;
    }

    public Ramen findByJan(String jan) {
        RamenMeta meta = RamenMeta.get();
        return Datastore.query(meta).filter(meta.jan.equal(jan)).asSingle();
    }

    public List<Ramen> getRamens() {
        return Datastore.query(Ramen.class).asList();
    }

    private byte[] resizeImage(byte[] src) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image oldImage = ImagesServiceFactory.makeImage(src);
        Transform resize = ImagesServiceFactory.makeResize(144, 144);
        Image newImage = imagesService.applyTransform(resize, oldImage);
        return newImage.getImageData();
    }
}
