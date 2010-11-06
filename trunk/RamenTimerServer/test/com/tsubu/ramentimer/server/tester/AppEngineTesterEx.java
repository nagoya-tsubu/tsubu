package com.tsubu.ramentimer.server.tester;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.slim3.tester.AppEngineTester;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesServicePb;
import com.google.appengine.repackaged.com.google.protobuf.ByteString;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Environment;

public class AppEngineTesterEx extends AppEngineTester {
    
    private static final String IMAGE_SERVICE = "images";
    
    public final ImageServiceStub imageServiceStub = new ImageServiceStub();
    
    @Override
    public byte[] makeSyncCall(Environment env, String service, String method, byte[] requestBuf)
            throws ApiProxyException {
        if (service.equals(IMAGE_SERVICE)) {
            Queue<byte[]> queue = imageServiceStub.imageStore.get(method);
            if (queue == null || queue.isEmpty()) {
                throw new ApiProxyException("service=" + service + ", method=" + method);
            }
            Image image = ImagesServiceFactory.makeImage(queue.peek());
            ImagesServicePb.ImageData.Builder imageData = ImagesServicePb.ImageData.newBuilder();
            BlobKey blobKey = image.getBlobKey();
            if(blobKey != null) {
                imageData.setBlobKey(image.getBlobKey().getKeyString());
                imageData.setContent(ByteString.EMPTY);
            } else {
                imageData.setContent(ByteString.copyFrom(image.getImageData()));
            }
            ImagesServicePb.ImagesTransformResponse.Builder response = ImagesServicePb.ImagesTransformResponse.newBuilder();
            response.setImage(imageData.build());
            return response.build().toByteArray();
        }
        return super.makeSyncCall(env, service, method, requestBuf);
    }
    
    public static class ImageServiceStub {
        Map<String, Queue<byte[]>> imageStore = new HashMap<String, Queue<byte[]>>();

        public void register(String method, byte[] imageData) {
            Queue<byte[]> queue = imageStore.get(method);
            if (queue == null) {
                queue = new LinkedList<byte[]>();
                imageStore.put(method, queue);
            }
            queue.add(imageData);
        }
    }
}
