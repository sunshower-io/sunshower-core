package io.sunshower.service.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by haswell on 2/17/17.
 */
public class RegexMatcher extends BaseMatcher<String> {

    private final String regex;

    public RegexMatcher(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean matches(Object item) {
        return item.toString().matches(regex);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches:");

    }

    public static Matcher<String> matches(String regex) {
        return new RegexMatcher(regex);
    }
}
