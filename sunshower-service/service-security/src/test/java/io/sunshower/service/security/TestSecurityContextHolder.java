package io.sunshower.service.security;

import java.lang.reflect.Field;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

/** Created by haswell on 5/12/17. */
public final class TestSecurityContextHolder implements SecurityContextHolderStrategy {

  public static void initialize() {
    try {
      Field strategy = SecurityContextHolder.class.getDeclaredField("strategy");
      strategy.setAccessible(true);
      strategy.set(SecurityContextHolder.class, new TestSecurityContextHolder());
    } catch (Exception ex) {

      ex.printStackTrace();
    }
  }

  // ~ Static fields/initializers
  // =====================================================================================

  private static SecurityContext contextHolder;

  // ~ Methods
  // ========================================================================================================

  public void clearContext() {}

  public static void clear() {
    contextHolder = null;
  }

  public SecurityContext getContext() {
    if (contextHolder == null) {
      contextHolder = new SecurityContextImpl();
    }

    return contextHolder;
  }

  public void setContext(SecurityContext context) {
    Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
    contextHolder = context;
  }

  public SecurityContext createEmptyContext() {
    return new SecurityContextImpl();
  }
}
