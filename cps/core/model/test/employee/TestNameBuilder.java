
package cps.core.model.test.employee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import cps.core.model.employee.PersonName;

import org.junit.Test;

public class TestNameBuilder {

  @Test(expected = IllegalArgumentException.class)
  public void testNullFirstName() {
    new PersonName.NameBuilder(null, "valid").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyFirstName() {
    new PersonName.NameBuilder("", "valid").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullLastName() {
    new PersonName.NameBuilder("valid", null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyLastName() {
    new PersonName.NameBuilder("valid", "").build();
  }

  @Test
  public void testCorrectInstantiation() {
    final PersonName name = new PersonName.NameBuilder("validfirst", "validlast").prefix(null)
        .suffix("").middleName("validmiddle").build();
    assertNotNull(name);
    assertEquals("", name.getPrefix());
    assertEquals("validfirst", name.getFirstName());
    assertEquals("validmiddle", name.getMiddleName());
    assertEquals("validlast", name.getLastName());
    assertEquals("", name.getSuffix());
  }

}
