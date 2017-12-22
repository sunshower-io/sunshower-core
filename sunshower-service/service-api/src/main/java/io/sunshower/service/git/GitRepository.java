package io.sunshower.service.git;

import io.sunshower.service.revision.model.Repository;
import io.sunshower.service.revision.model.Revision;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.locks.Lock;

/**
 * Created by haswell on 5/22/17.
 */
public interface GitRepository extends AutoCloseable {

    
    boolean isClosed();

    void open();

    Lock lock();

    void unlock();

    void close() throws Exception;
    
    File getLocal();
    
    boolean isLocked(boolean localOnly);
    
    boolean isLocked();

    File write(
            String name,
            Reader reader
    );


    File write(
            String name,
            InputStream inputStream
    );




    void initialize();

    Revision commit();

    Revision commit(String message);


    File read(File p);

    InputStream read(String path);

    void checkout(Revision commit);

    boolean exists(String path);
    
}
