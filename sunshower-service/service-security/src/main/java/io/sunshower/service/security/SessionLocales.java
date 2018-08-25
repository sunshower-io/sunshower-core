package io.sunshower.service.security;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SessionLocales {

  private static final ThreadLocal<List<Locale>> contextHolder = new InheritableThreadLocal<>();

  public static void setLocales(Locale... locales) {
    contextHolder.set(Arrays.asList(locales));
  }

  public static List<Locale> getLocales() {
    return contextHolder.get();
  }

  public static void setLocales(List<Locale> acceptableLanguages) {
    contextHolder.set(acceptableLanguages);
  }
}
