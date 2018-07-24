package io.sunshower.service.signup;

import io.sunshower.security.api.ProductService;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

public class Products implements ApplicationListener<ContextRefreshedEvent>, ProductService {

  @Inject private UserTransaction userTransaction;
  @PersistenceUnit private EntityManagerFactory entityManagerFactory;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) {
    try {
      onSave();
    } catch (SystemException e) {
      throw new RuntimeException(e);
    }
  }

  @PostConstruct
  @Transactional
  public void onSave() throws SystemException {
    final EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      userTransaction.begin();

      List<Product> products =
          Arrays.asList(
              new Product(
                  "stratosphere:design",
                  "Low-code Deployment across Clouds with Stratosphere:Design"),
              new Product(
                  "stratosphere:discover",
                  "Cross-cloud infrastructure "
                      + "discovery and management with Stratosphere:Discover"),
              new Product("anvil", "Cross-cloud Infrastructure Optimization"));
      for (Product product : products) {
        entityManager.persist(product);
      }
      entityManager.flush();
      userTransaction.commit();
    } catch (Exception e) {
      userTransaction.rollback();
    } finally {
      entityManager.close();
    }
  }
}
