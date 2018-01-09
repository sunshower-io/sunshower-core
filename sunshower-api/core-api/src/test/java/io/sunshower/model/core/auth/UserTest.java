package io.sunshower.model.core.auth;

import static java.lang.System.out;

import java.util.Collections;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Test;

/** Created by haswell on 10/23/16. */
public class UserTest {

  @Test
  public void ensureUserIsSerializedProperly() throws JAXBException {

    javax.xml.bind.JAXBContext context =
        JAXBContextFactory.createContext(new Class[] {User.class}, Collections.emptyMap());
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty("eclipselink.media-type", "application/json");
    //        marshaller.setProperty("eclipselink.json.include-root", false);
    User u = new User();
    u.setUsername("Josiah");
    u.setPassword("password");
    marshaller.marshal(u, out);
  }
}
