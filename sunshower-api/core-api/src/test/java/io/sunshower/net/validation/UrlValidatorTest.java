package io.sunshower.net.validation;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by haswell on 5/22/17.
 */
public class UrlValidatorTest {

    private UrlValidator urlValidator;

    class Person implements Comparable<Person> {
        int amt;

        @Override
        public int compareTo(@NotNull Person o) {
            return Integer.compare(amt, o.amt);
        }
    }


//    Anne, Lisa, Joe, Peter, Lily, Frapper
//    [10, 15, 75, 25, 90, 10]

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

    @Before
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