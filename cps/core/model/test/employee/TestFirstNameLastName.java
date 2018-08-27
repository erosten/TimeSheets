
package cps.core.model.test.employee;

import static org.junit.Assert.assertEquals;

import cps.core.model.employee.PersonName;

import org.junit.Test;

public class TestFirstNameLastName {

  @Test
  public void testCorrectFirstNameLastName() {
    final PersonName name = new PersonName.NameBuilder("kevin", "hanley").prefix(null).suffix("Jr.")
        .middleName(null).build();
    assertEquals("hanley, kevin Jr.", (name.getFullNameLastFirst()));
    assertEquals("hanley, kevin Jr.", name.withPrefix("Mr.").getFullNameLastFirst());
    assertEquals("hanley, kevin Sr.", name.withPrefix("").withSuffix("Sr.").getFullNameLastFirst());
    assertEquals("hanley, kevin", name.withSuffix(null).getFullNameLastFirst());
  }
}
