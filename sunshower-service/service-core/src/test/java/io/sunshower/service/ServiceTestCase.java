package io.sunshower.service;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.service.security.SecurityConfiguration;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import io.sunshower.test.common.TestConfigurationConfiguration;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.junit.After;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Transactional
@ExtendWith(SpringExtension.class)
@RunWith(JUnitPlatform.class)
@ContextConfiguration(classes = {
        TestConfigurationConfiguration.class,
        HibernateConfiguration.class,
        PersistenceConfiguration.class,
        CoreServiceConfiguration.class,
        DataSourceConfiguration.class,
        FlywayConfiguration.class,
        SecurityConfiguration.class,
        TestConfiguration.class,
})
@SuppressWarnings("all")
public abstract class ServiceTestCase extends SerializationTestCase {

    @PersistenceContext
    protected EntityManager entityManager;

    @PersistenceUnit
    protected EntityManagerFactory entityManagerFactory;

    @Inject
    protected FullTextEntityManager fullTextEntityManager;


    @Inject
    protected PlatformTransactionManager platformTransactionManager;

    private final Set<Object> saved = new HashSet<>();

    public ServiceTestCase(SerializationAware.Format format, Class[] bound) {
        super(format, bound);
    }

    public ServiceTestCase(SerializationAware.Format format, boolean includeRoot, Class[] bound) {
        super(format, includeRoot, bound);
    }


    protected TransactionTemplate newTransactionTemplate() {
        return newTransactionTemplate(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
    }

    protected TransactionTemplate newTransactionTemplate(int def) {
        final TransactionTemplate template = new TransactionTemplate(platformTransactionManager);
        template.setPropagationBehavior(def);
        return template;
    }

    protected <T> T save(T value) {
        final EntityManager transactionEntityManager =
                entityManagerFactory.createEntityManager();
        EntityTransaction transaction = transactionEntityManager.getTransaction();
        transaction.begin();
        try {
            return newTransactionTemplate().execute(t -> {
                transactionEntityManager.persist(value);
                saved.add(value);
                return value;
            });
        } finally {
            try {
                if (!transaction.getRollbackOnly()) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } finally {
                transactionEntityManager.close();
            }
        }
    }


    protected <T> T transactionally(Supplier<T> f) {
        return new TransactionTemplate(platformTransactionManager).execute(t -> {
            return f.get();
        });
    }

    protected <T> List<T> saveAll(T... values) {
        final EntityManager transactionEntityManager =
                entityManagerFactory.createEntityManager();
        EntityTransaction transaction = transactionEntityManager.getTransaction();
        transaction.begin();
        try {
            return newTransactionTemplate().execute(t -> {
                List<T> results = new ArrayList<>();
                for (T value : values) {
                    index(value.getClass(), value);
                    transactionEntityManager.persist(value);
                    saved.add(value);
                    results.add(value);
                }
                return results;
            });
        } finally {

            try {
                if (!transaction.getRollbackOnly()) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } finally {
                transactionEntityManager.close();
            }
        }
    }

    private <T> void index(Class<?> aClass, T value) {
        if (aClass.isAnnotationPresent(Indexed.class)) {
            fullTextEntityManager.merge(value);
        }
    }

    protected void clear() {
        final EntityManager transactionEntityManager =
                entityManagerFactory.createEntityManager();
        EntityTransaction transaction = transactionEntityManager.getTransaction();
        transaction.begin();
        try {
            newTransactionTemplate().execute(t -> {
                for (Object value : saved) {
                    transactionEntityManager.remove(transactionEntityManager.merge(value));
                }
                return null;
            });
            saved.clear();
        } finally {
            try {
                if (!transaction.getRollbackOnly()) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } finally {
                transactionEntityManager.close();
            }
        }
    }


    @After
    public void tearDown() {
        clear();
    }




    protected static byte[] readBytes(String path) {
        try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream(path)) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static boolean exists(String property, String s) {
        return new java.io.File(property, s).exists();
    }

//    protected Credential fromHome(String s) {
//        try {
//            String home = System.getProperty("user.home");
//            String awsHome = String.format("%s/%s", home, ".aws");
//            final FileInputStream fis = new FileInputStream(new java.io.File(awsHome, s));
//            return read(fis, Credential.class);
//        } catch (FileNotFoundException ex) {
//            throw new IllegalStateException(ex);
//        }
//    }
}
