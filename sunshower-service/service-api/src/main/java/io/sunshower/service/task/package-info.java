/** Created by haswell on 3/26/17. */
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters(
    @XmlJavaTypeAdapter(value = IdentifierConverter.class, type = Identifier.class))
package io.sunshower.service.task;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.IdentifierConverter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
