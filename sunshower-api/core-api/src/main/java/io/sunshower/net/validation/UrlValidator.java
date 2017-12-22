package io.sunshower.net.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by haswell on 5/22/17.
 */
public class UrlValidator implements ConstraintValidator<Url, String> {

    static final Pattern pattern = Pattern.compile(
            "^((http[s]?|ftp):/)?/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$"
    );


    @Override
    public void initialize(Url constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && pattern.matcher(value).matches();
    }
}
