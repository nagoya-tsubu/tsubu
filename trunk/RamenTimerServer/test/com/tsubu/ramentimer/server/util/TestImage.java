package com.tsubu.ramentimer.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestImage {
    
    public static byte[] load(String resource) {
        FileChannel channel = null;
        try {
            File file = new File(TestImage.class.getResource(resource).toURI());
            channel = new FileInputStream(file).getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int) file.length());
            channel.read(buf);
            return buf.array();
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        } finally {
            try {
                if (channel != null) channel.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }
}
