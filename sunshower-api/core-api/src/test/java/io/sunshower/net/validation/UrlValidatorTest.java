package io.sunshower.net.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UrlValidatorTest {

  private UrlValidator urlValidator;

  class Person implements Comparable<Person> {
    int amt;

    @Override
    public int compareTo(@Nonnull Person o) {
      return Integer.compare(amt, o.amt);
    }
  }

  @Test
  public void checkGoogler() {

    SortedSet<Person> ps = new TreeSet<>();

    Person fst = new Person();
    fst.amt = 10;

    Person snd = new Person();
    snd.amt = 10;

    ps.add(fst);
    ps.add(snd);

    assertThat(ps.size(), is(1));
  }

  @BeforeEach
  public void setUp() {
    urlValidator = new UrlValidator();
  }

  @Test
  public void ensureValidatorMatchesSimpleUrl() {
    assertTrue(urlValidator.isValid("~/Coolbeans", null));
  }

  @Test
  public void ensureValidatorMatchesCOmplexUrls() {
    assertTrue(urlValidator.isValid("http://www.google.com/../Coolbeans?frap=dap&nap=gap", null));
  }

  @Test
  public void ensureAbsoluteFilePathWorks() {
    assertTrue(urlValidator.isValid("/Coolbeans/beanbeans", null));
  }
}
