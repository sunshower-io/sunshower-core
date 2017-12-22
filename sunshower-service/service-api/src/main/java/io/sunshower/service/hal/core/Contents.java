package io.sunshower.service.hal.core;

import org.springframework.util.FastByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by haswell on 10/14/17.
 */
public class Contents {
    
    public static InputStream openString(String value) {
        return new ByteArrayInputStream(value.getBytes());
    }
    
    public static String readString(InputStream is) throws IOException {
        return new String(read(is));
    }
    
    public static byte[] read(InputStream v) throws IOException {
        final FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = v.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        return output.toByteArrayUnsafe();
    }
}
