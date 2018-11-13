package io.sunshower.service.serialization;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MOXyJsonGraphContextTest {

  private MOXyJsonGraphContext context;
  private DynamicResolvingMoxyJsonProvider provider;

  @BeforeEach
  public void setUp() {

    DynamicJaxrsProviders providers = new DynamicJaxrsProviders();
    provider = new DynamicResolvingMoxyJsonProvider(providers);
    context = new MOXyJsonGraphContext(provider, providers);
  }

  @Test
  void ensureReadingNpeGraphWorks() {
    val g = context.load(ClassLoader.getSystemResourceAsStream("graphs/npe-1.json"));
    System.out.println(g);
  }
}
