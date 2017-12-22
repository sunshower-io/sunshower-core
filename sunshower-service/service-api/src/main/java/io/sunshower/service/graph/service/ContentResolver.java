package io.sunshower.service.graph.service;



import io.sunshower.service.hal.core.Content;

import java.io.InputStream;
import java.io.Reader;

/**
 * Created by haswell on 10/13/17.
 */
public interface ContentResolver {
    
    InputStream read();
    
    void write(InputStream inputStream);
    
    void write(byte[] data);
    
    void write(Reader reader);
    
    void write(String data);

    Content getWorkingContent();
    
}
