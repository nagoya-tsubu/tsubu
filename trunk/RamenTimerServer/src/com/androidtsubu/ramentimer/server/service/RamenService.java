package com.androidtsubu.ramentimer.server.service;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slim3.controller.ControllerConstants;
import org.slim3.controller.upload.FileItem;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.S3QueryResultList;
import org.slim3.memcache.Memcache;
import org.slim3.util.ApplicationMessage;
import org.slim3.util.BeanUtil;
import org.slim3.util.RequestMap;

import com.androidtsubu.ramentimer.server.meta.RamenMeta;
import com.androidtsubu.ramentimer.server.model.Ramen;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

public class RamenService {
    private static final String MEMCACHE_KEY_COUNT = "ramenCount";

    public Ramen create(HttpServletRequest request) {
        ApplicationMessage.setBundle(
            ControllerConstants.DEFAULT_LOCALIZATION_CONTEXT,
            Locale.JAPAN); // TODO とりあえず日本語限定 Android側でLocaleがセットされているか調査
//            request.getLocale());

        return create(new RequestMap(request));
    }

    public Ramen create(HashMap<String, Object> input) {
        input.put(ControllerConstants.ERRORS_KEY, new Errors());
        Validators v = new Validators(input);
        v.add("name", v.required(), v.maxlength(100));
        v.add("jan", v.required(), v.maxlength(13));
        v.add("boilTime", v.required(), v.integerType(), v.longRange(1, 3540));
        v.add("twitterId", v.required(), v.integerType());
        v.add("image", v.required());
        if (v.validate() == false) {
            String msg = "";
            for (String error : v.getErrors().values()) {
                msg += error + "\n";
            }
            throw new IllegalArgumentException(msg);
        }

        if (findByJan(input.get("jan").toString()) != null) {
            throw new IllegalArgumentException("既に登録されています。");
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

    public int count() {
        int count = 0;
        if (Memcache.contains(MEMCACHE_KEY_COUNT)) {
            count = Memcache.get(MEMCACHE_KEY_COUNT);
        } else {
            count = Datastore.query(Ramen.class).count();
            Memcache.put(MEMCACHE_KEY_COUNT, count);
        }
        return count;
    }

    public S3QueryResultList<Ramen> recentList(int limit, String encodedCursor) {
        ModelQuery<Ramen> query = Datastore.query(Ramen.class)
            .sort(RamenMeta.get().createdAt.desc)
            .limit(limit);
        if (encodedCursor != null) {
            query.encodedStartCursor(encodedCursor);
        }
        return query.asQueryResultList();
    }

    private byte[] resizeImage(byte[] src) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image oldImage = ImagesServiceFactory.makeImage(src);
        Transform resize = ImagesServiceFactory.makeResize(144, 144);
        Image newImage = imagesService.applyTransform(resize, oldImage);
        return newImage.getImageData();
    }
}
