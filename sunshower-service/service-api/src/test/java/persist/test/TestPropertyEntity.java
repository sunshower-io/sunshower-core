package persist.test;

import io.sunshower.model.core.PropertyAwareObject;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TEST_PROPERTY_ENTITY", schema = "SUNSHOWER")
public class TestPropertyEntity extends PropertyAwareObject {}
