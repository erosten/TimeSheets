
package cps.core.model.test.employee;

import static org.junit.Assert.assertEquals;

import cps.core.model.employee.PersonName;

import org.junit.Test;

public class TestFullName {

  @Test
  public void testCorrectFullName() {
    final PersonName name = new PersonName.NameBuilder("first", "last").prefix(null).suffix("")
        .middleName("mid").build();
    assertEquals("first mid last", (name.getFullName()));
    assertEquals("Mr. first mid last", name.withPrefix("Mr.").getFullName());
    assertEquals("first mid last Sr.", name.withPrefix("").withSuffix("Sr.").getFullName());
    assertEquals("first mid last", name.withSuffix(null).getFullName());
  }
}
