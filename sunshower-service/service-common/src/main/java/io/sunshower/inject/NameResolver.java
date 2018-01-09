package io.sunshower.inject;

import java.lang.reflect.AccessibleObject;

/** Created by haswell on 3/5/17. */
public interface NameResolver {

  String resolve(AccessibleObject target);
}
