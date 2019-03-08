package io.sunshower.model.core.auth;

import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Test;

public class UserTest {

  @Test
  public void ensureDetailsAreNotNullByDefault() {
    assertThat(new User().getDetails().getEmailAddress(), is(nullValue()));
  }

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
