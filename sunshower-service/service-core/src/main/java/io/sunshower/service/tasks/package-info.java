/**
 * Created by haswell on 3/27/17.
 */

@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(
                type = Class.class,
                value = ClassAdapter.class
        ),
        @XmlJavaTypeAdapter(
                type = Identifier.class,
                value = IdentifierConverter.class
        )
})
@XmlAccessorType(XmlAccessType.FIELD)
package io.sunshower.service.tasks;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.ClassAdapter;
import io.sunshower.common.rs.IdentifierConverter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;