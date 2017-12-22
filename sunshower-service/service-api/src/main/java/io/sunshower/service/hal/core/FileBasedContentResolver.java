package io.sunshower.service.hal.core;

import io.reactivex.subjects.Subject;
import io.sunshower.common.Identifier;
import io.sunshower.common.io.ReaderInputStream;
import io.sunshower.model.core.io.File;
import io.sunshower.persist.Persistable;
import io.sunshower.service.graph.service.ContentResolver;
import io.sunshower.service.model.PropertyAwareObject;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.orchestration.model.TemplateEvent;
import io.sunshower.service.orchestration.model.TemplateEvents;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.file.Files;

/**
 * Created by haswell on 10/14/17.
 */
public class FileBasedContentResolver implements ContentResolver {
    
    final Content content;
    final OrchestrationTemplate entity;
    final Subject<TemplateEvent> topic;
    final Identifier targetId;
    final Class<? extends PropertyAwareObject> targetType;
    private final Element target;

    public FileBasedContentResolver(
            Identifier targetId,
            Class<? extends PropertyAwareObject> targetType,
            OrchestrationTemplate entity, 
            Content content, 
            Subject<TemplateEvent> topic,
            Element target
    ) {
        this.topic = topic;
        this.entity = entity;
        this.target = target;
        this.content = content;
        this.targetId = targetId;
        this.targetType = targetType;
    }


    @Override
    public InputStream read() {
        final File file = content.getFile();
        if(file != null) {
            try {
                return new BufferedInputStream(
                        new FileInputStream(
                                new java.io.File(file.getPath())));
            } catch (FileNotFoundException e) {
            }
        }
        throw new EntityNotFoundException("No file for that content is available!");
    }

    @Override
    public void write(InputStream value) {
        final File file = content.getFile();
        if(file != null) {
            final java.io.File f = new java.io.File(file.getPath());
            try {
                checkFile(f);
                byte[] data = Contents.read(value);
                Files.write(f.toPath(), data);
                topic.onNext(TemplateEvents.contentWritten(
                        content, 
                        entity, 
                        new String(data), 
                        targetId, 
                        targetType,
                        target
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new EntityNotFoundException("No file for that content is available!");
        }
    }

    @Override
    public void write(byte[] data) {
        write(new ByteArrayInputStream(data));
    }

    @Override
    public void write(Reader reader) {
        write(new ReaderInputStream(reader));
    }

    @Override
    public void write(String data) {
        write(Contents.openString(data));
    }

    @Override
    public Content getWorkingContent() {
        return content;
    }

    private void checkFile(java.io.File f) throws IOException {
        if(!f.exists()) {
            final java.io.File parent = f.getParentFile();
            if(!(parent.isDirectory() || parent.mkdirs())) {
                throw new RuntimeException("Error--failed to create file (check permissions?)");
            }
            if(!(f.exists() || f.createNewFile())) {
                throw new RuntimeException("Failed to create file " + f.getAbsolutePath());
            }
        }
    }
}
