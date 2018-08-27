
package cps.core.model.test.employee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import cps.core.ProgramPortal;
import cps.core.db.frame.DerbyDatabase;
import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.PersonName;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// this test assumes that builders & all methods work for the following classes:
// Employee, EmployeeWage, Wage Interface, PersonName,EmployeeWageChange

// this method tests primarily the EmployeeHandler
public class TestEmployeeCRUD {

  private static ProgramPortal eh;
  private static DerbyDatabase testDB;
  private static Employee emp = null;
  private static Employee.Builder eb;

  @BeforeClass
  public static void loadDBConnection() throws SQLException {
    testDB = DerbyDatabase.TestDB;
    eh = new ProgramPortal(testDB);
  }

  @AfterClass
  public static void wipeDB() throws SQLException {
    testDB.reset();
    testDB.shutdown();
  }

  /**
   * Sets up an Employee to work with.
   */
  @Before
  public void setUpEmployee() {
    eb = new Employee.Builder(new PersonName.NameBuilder("Kevin", "Hanley").build(), "KLH");
    eb.langBonus(new BigDecimal("0.5")).wage(new EmployeeWage.Builder(
        "chief",
        new BigDecimal("100000")));
    eb.wage(new EmployeeWage.Builder("travel rate", new BigDecimal("500")));
    emp = eb.build();
    assertTrue(eh.addNewEmployee(emp));
    assertEquals(emp, eh.findEmployeeById(emp.getId()));
    assertEquals(emp, eh.findEmployeeByAbbreviation(emp.getAbbreviation()));
  }

  @After
  public void cleanup() {
    testDB.reset();
    emp = null;
  }

  // testing add
  @Test
  public void testEmployeeAddWithSameAbbreviationReturnsFalse() {
    // should return false here
    final String before = testDB.getSnapshot(false);
    assertFalse(eh.addNewEmployee(eb.build()));
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testIdenticalEmployeeAddReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    assertTrue(eh.addNewEmployee(test));
    final String before = testDB.getSnapshot(false);
    assertFalse(eh.addNewEmployee(test));
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testAllButAbbreviationAndIdSameIsOkay() {
    assertTrue(eh.addNewEmployee(eb.abbreviation("REk").build()));
  }

  @Test(expected = NullPointerException.class)
  public void testEmployeeAddNullThrowsNullPointerException() {
    final String before = testDB.getSnapshot(false);
    eh.addNewEmployee(null);
    assertTrue(before.equals(testDB.getSnapshot(false)));
  }

  // testing termination

  // tests both object and id versions
  @Test
  public void testRegularTerminateEmployeeBothVersions() {
    eh.terminateEmployee(emp);
    assertTrue(emp.isTerminated());
    final Employee employee2 = eb.abbreviation("test").build();
    assertTrue(eh.addNewEmployee(employee2));
    assertEquals(employee2, eh.findEmployeeById(employee2.getId()));
    eh.terminateEmployee(employee2);
    assertTrue(employee2.isTerminated());
  }

  @Test(expected = NullPointerException.class)
  public void testNullRemoveObjectThrowsNullPointerException() {
    final Employee employee = null;
    // should throw null pointer here
    eh.terminateEmployee(employee);
  }

  /**
   * tests that removing an employee with a null object throws a NullPointerException.
   */
  public void testNullRemoveWithNullIdThrowsNullPointerException() {
    final String empId = null;
    // should throw null pointer here
    try {
      eh.terminateEmployee(empId);
    } catch (final NullPointerException npe) {
      assertEquals("Employee found from id was null", npe.getMessage());
    }
  }

  /**
   * tests that terminating an employee that is already terminated does nothing.
   */
  public void testTerminatingTerminatedEmployeeDoesNothing() {
    eh.terminateEmployee(emp);
    final String snapshot = testDB.getSnapshot(false);
    // should do nothing
    eh.terminateEmployee(emp);
    assertEquals(snapshot, testDB.getSnapshot(false));
  }

  // testing name edits
  @Test
  public void testFirstNameEdit() {
    emp.setName(emp.getName().withFirstName("Erik"));
    eh.save();
    assertEquals("Erik", emp.getName().getFirstName());
  }

  @Test
  public void testFirstNameEditOnNonManagedReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setName(test.getName().withFirstName("Erik"));
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testLastNameEdit() {
    emp.setName(emp.getName().withLastName("Rosten"));
    eh.save();
    assertEquals("Rosten", emp.getName().getLastName());
  }

  @Test
  public void testLastNameEditOnNonManagedReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setName(test.getName().withLastName("Rosten"));
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testPrefixEdit() {
    emp.setName(emp.getName().withPrefix("Mr."));
    eh.save();
    assertEquals("Mr.", emp.getName().getPrefix());
  }

  @Test
  public void testPrefixEditOnNonManagedReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setName(test.getName().withPrefix("Mr."));
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testMiddleNameEdit() {
    emp.setName(emp.getName().withMiddleName("Stover"));
    eh.save();
    assertEquals("Stover", eh.findEmployeeById(emp.getId()).getName().getMiddleName());
  }

  @Test
  public void testMiddleNameEditOnNonManagedReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setName(test.getName().withMiddleName("Stover"));
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testSuffixEdit() {
    emp.setName(emp.getName().withSuffix("the 3rd"));
    eh.save();
    assertEquals("the 3rd", eh.findEmployeeById(emp.getId()).getName().getSuffix());
  }

  @Test
  public void testSuffixEditEditOnNonManagedReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setName(emp.getName().withMiddleName("the 21st"));
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));
  }

  // testing abbreviation edits
  @Test
  public void testAbbreviationEditOnNonManagedDoesNothing() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setAbbreviation("lel");
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));

  }

  @Test
  public void testAbbreviationEdit() {
    emp.setAbbreviation("lel");
    eh.save();
    assertEquals("lel", eh.findEmployeeById(emp.getId()).getAbbreviation());
  }

  // testing lang edits
  @Test
  public void testLangEdit() {
    emp.setLang(new BigDecimal("20"));
    eh.save();
    assertEquals(new BigDecimal("20"), eh.findEmployeeById(emp.getId()).getLanguageBonus());
  }

  @Test
  public void testLangEditOnNonManagedReturnsFalse() {
    final Employee test = eb.abbreviation("test").build();
    final String before = testDB.getSnapshot(false);
    test.setLang(new BigDecimal("0.08"));
    eh.save();
    assertEquals(before, testDB.getSnapshot(false));
  }

  // testing contains
  @Test
  public void testEmployeeObjectContains() {
    assertTrue(eh.containsEmployee(emp));
    // editting a managed employee should still return true
    emp.setAbbreviation("test");
    eh.save();
    assertTrue(eh.containsEmployee(emp));
    // should not contain a new employee object even if identical abbreviation
    // (id's can never be same)
    final String before = testDB.getSnapshot(false);
    final Employee test = eb.build();
    assertFalse(eh.containsEmployee(test));
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testEmployeeIdContains() {
    assertTrue(eh.containsEmployee(emp.getId()));
    // editting a managed employee should still return true
    emp.setAbbreviation("REk");
    eh.save();
    assertTrue(eh.containsEmployee(emp.getId()));
    // should not contain a new employee object even if identical abbreviation
    // (id's can never be same)
    final String before = testDB.getSnapshot(false);
    assertFalse(eh.containsEmployee(eb.build()));
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testEmployeeAbbreviationContains() {
    assertTrue(eh.findEmployeeByAbbreviation(emp.getAbbreviation()) != null);
    assertTrue(eh.containsEmployeeByAbbreviation(emp.getAbbreviation()));
    // editting a managed employee should still return true
    emp.setAbbreviation("REk");
    eh.save();
    assertTrue(eh.containsEmployeeByAbbreviation(emp.getAbbreviation()));
    // should not contain a new employee object even if identical abbreviation
    // (id's can never be same)
    final String before = testDB.getSnapshot(false);
    assertFalse(eh.containsEmployeeByAbbreviation(eb.build().getAbbreviation()));
    assertEquals(before, testDB.getSnapshot(false));
  }

  // testing wage methods
  // testing wage add
  @Test
  public void testEmployeeWageAdd() {
    final EmployeeWage.Builder ewb = new EmployeeWage.Builder("test rate", new BigDecimal("1000"))
        .employee(emp);
    final EmployeeWage employeeWage = ewb.build();
    assertTrue(eh.addNewEmployeeWage(employeeWage));
    assertEquals(employeeWage, eh.findWageByName(emp, "test rate"));
    assertEquals(employeeWage, eh.findWageByName(emp.getAbbreviation(), "test rate"));
    assertEquals(emp, eh.findWageByName(emp, "test rate").getEmployee());
    // same id returns false
    employeeWage.setRate(BigDecimal.TEN);
    eh.save();
    final String before = testDB.getSnapshot(false);
    assertFalse(eh.addNewEmployeeWage(employeeWage));
    // same name returns false
    assertFalse(eh.addNewEmployeeWage(new EmployeeWage.Builder("test rate", BigDecimal.TEN)
        .employee(emp).build()));
    assertEquals(before, testDB.getSnapshot(false));
    // everything else is fine
    assertTrue(eh.addNewEmployeeWage(new EmployeeWage.Builder("lol", new BigDecimal("1000"))
        .employee(emp).build()));
  }

  @Test(expected = NullPointerException.class)
  public void testEmployeeWageAddNullThrowsNullPointer() {
    final String before = testDB.getSnapshot(false);
    eh.addNewEmployeeWage(null);
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testEmployeeWageAddWithNonManagedEmployee() {
    final String before = testDB.getSnapshot(false);
    final Employee test = eb.abbreviation("test").build();
    final EmployeeWage employeeWage = new EmployeeWage.Builder("test rate", new BigDecimal("1000"))
        .employee(test).build();
    assertFalse(eh.addNewEmployeeWage(employeeWage));
    assertNull(eh.findWageByName(emp, "test rate"));
    assertNull(eh.findWageByName(emp.getAbbreviation(), "test rate"));
    assertEquals(before, testDB.getSnapshot(false));

  }

  // test wage remove
  @Test
  public void testEmployeeWageRemove() {
    final String before = testDB.getSnapshot(false);
    final EmployeeWage wage = new EmployeeWage.Builder("test rate", new BigDecimal("500")).employee(
        emp).build();
    assertTrue(eh.addNewEmployeeWage(wage));
    // no sql errors, wage to remove was found
    assertTrue(eh.removeEmployeeWage(wage));
    assertFalse(eh.removeEmployeeWage(wage));
    // check not on the Employee Wage table
    assertNull(eh.findWageByName(emp.getAbbreviation(), "test rate"));
    assertFalse(eh.findEmployeeByAbbreviation(emp.getAbbreviation()).getWages().contains(wage));
    // wage to remove should not be found
    assertFalse(eh.removeEmployeeWage(wage));
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testEmployeeWageRemoveWithNonManagedEmployeeReturnsFalse() {
    final String before = testDB.getSnapshot(false);
    final Employee test = eb.abbreviation("test").build();
    final EmployeeWage employeeWage = new EmployeeWage.Builder("test rate", new BigDecimal("1000"))
        .employee(test).build();
    assertFalse(eh.removeEmployeeWage(employeeWage));
    assertNull(eh.findWageByName(emp, "test rate"));
    assertNull(eh.findWageByName(emp.getAbbreviation(), "test rate"));
    assertEquals(before, testDB.getSnapshot(false));
  }
  // test wage edits

  // edit wage rate
  @Test
  public void testEmployeeWageRateEdit() {
    final EmployeeWage employeeWage = new EmployeeWage.Builder("test rate", new BigDecimal("500"))
        .employee(emp).build();
    assertTrue(eh.addNewEmployeeWage(employeeWage));
    employeeWage.setRate(new BigDecimal("21"));
    eh.save();
    final String before = testDB.getSnapshot(false);
    assertEquals(employeeWage.getRate(), eh.findWageByName(emp, "test rate").getRate());
    assertEquals(employeeWage.getRate(), eh.findWageByName(emp.getAbbreviation(), "test rate")
        .getRate());
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testEmployeeWageRateEditOnNonManagedReturnsFalse() {
    final String before = testDB.getSnapshot(false);
    final Employee test = eb.abbreviation("test").build();
    final EmployeeWage employeeWage = new EmployeeWage.Builder("travel rate", new BigDecimal("500"))
        .employee(test).build();
    employeeWage.setRate(new BigDecimal("21"));
    eh.save();
    assertNull(eh.findWageByName(test, "travel rate"));
    assertNull(eh.findWageByName(test.getAbbreviation(), "travel rate"));
    assertEquals(before, testDB.getSnapshot(false));
  }

  // check wagechanges
  @Test
  public void testCreationDateEqualsTodayEqualsLastEditDate() {
    final EmployeeWage wage = new EmployeeWage.Builder("testdate", new BigDecimal(21)).employee(
        new Employee.Builder(new PersonName.NameBuilder("erik", "test").build(), "blahh").build())
        .build();
    assertTrue(wage.getDateCreated().equals(DateTime.now().toLocalDate()));
    assertTrue(wage.getDateOfLastRateChange().equals(DateTime.now().toLocalDate()));
    assertTrue(wage.getDateCreated().equals(wage.getDateOfLastRateChange()));
  }

  @Test
  public void testOneWageChangeValues() {
    final EmployeeWage wage = new EmployeeWage.Builder("testwagechange", new BigDecimal(21))
        .employee(emp).build();
    assertTrue(eh.addNewEmployeeWage(wage));
    wage.setRate(new BigDecimal(31));
    eh.save();
    assertTrue(wage.getChanges().get(0).getNewWageRate().equals(new BigDecimal("21")));
    assertTrue(wage.getChanges().get(0).getDate().equals(DateTime.now().toLocalDate()));
    assertTrue(wage.getChanges().get(1).getNewWageRate().equals(new BigDecimal("31")));
    assertTrue(wage.getChanges().get(1).getDate().equals(DateTime.now().toLocalDate()));
  }

  @Test
  public void testFiftyWageChanges() {
    final EmployeeWage wage = new EmployeeWage.Builder("testwagechange", new BigDecimal(21))
        .employee(emp).build();
    assertTrue(eh.addNewEmployeeWage(wage));
    for (int i = 0; i < 50; i++) {
      wage.setRate(new BigDecimal(i));
      eh.save();
    }
    for (int i = 0; i < wage.getChanges().size(); i++) {
      if (i == 0) {
        assertEquals(wage.getChanges().get(0).getNewWageRate(), new BigDecimal(21));
        assertEquals(wage.getChanges().get(0).getDate(), DateTime.now().toLocalDate());
      } else {
        assertEquals(wage.getChanges().get(i).getNewWageRate(), new BigDecimal(i - 1));
        assertEquals(wage.getChanges().get(i).getDate(), DateTime.now().toLocalDate());
      }
    }
  }

  // check wage name edit
  @Test
  public void testEmployeeWageNameEdit() {
    final EmployeeWage wage = new EmployeeWage.Builder("test rate", new BigDecimal("500")).employee(
        emp).build();
    assertTrue(eh.addNewEmployeeWage(wage));
    wage.setName("testing");
    eh.save();
    final String before = testDB.getSnapshot(false);
    assertEquals(wage.getName(), eh.findWageByName(emp, "testing").getName());
    assertEquals(wage.getName(), eh.findWageByName(emp.getAbbreviation(), "testing").getName());
    assertEquals(wage, eh.findWageById(wage.getId()));
    assertEquals(before, testDB.getSnapshot(false));
  }

  @Test
  public void testEmployeeWageNameEditOnNonManagedReturnsFalse() {
    final String before = testDB.getSnapshot(false);
    final Employee test = eb.abbreviation("test").build();
    final EmployeeWage employeeWage = new EmployeeWage.Builder("travel rate", new BigDecimal("500"))
        .employee(test).build();
    employeeWage.setName("testing");
    eh.save();
    assertNull(eh.findWageByName(test, "travel rate"));
    assertNull(eh.findWageByName(test.getAbbreviation(), "travel rate"));
    assertNull(eh.findWageById(employeeWage.getId()));
    assertEquals(before, testDB.getSnapshot(false));
  }

  // test finders
  @Test
  public void testEmployeeFinders() {
    // testing id first
    assertEquals(emp, eh.findEmployeeById(emp.getId()));
    assertNull(eh.findEmployeeById("random"));
    assertNull(eh.findEmployeeById(null));
    // testing abbreviation
    assertEquals(emp, eh.findEmployeeByAbbreviation(emp.getAbbreviation()));
    // case insensitive
    assertEquals(emp, eh.findEmployeeByAbbreviation(emp.getAbbreviation().toLowerCase()));
    assertNull(eh.findEmployeeByAbbreviation(null));
    assertNull(eh.findEmployeeByAbbreviation("wrong"));
  }

  @Test
  public void testEmployeeWageFinders() {
    // testing id first
    for (final EmployeeWage ew : emp.getWages()) {
      assertEquals(ew, eh.findWageById(ew.getId()));
    }
    assertNull(eh.findWageById(null));
    assertNull(eh.findWageById("randy"));
    // testing name find
    for (final EmployeeWage ew : emp.getWages()) {
      assertEquals(ew, eh.findWageByName(emp.getAbbreviation(), ew.getName()));
    }
    for (final EmployeeWage ew : emp.getWages()) {
      // case insensitive
      assertEquals(ew, eh.findWageByName(emp.getAbbreviation(), ew.getName().toLowerCase()));
    }
    // testing employee object find
    for (final EmployeeWage ew : emp.getWages()) {
      assertEquals(ew, eh.findWageByName(emp, ew.getName()));
    }
    // null object input returns null prohibited by language
    // null name returns null
    assertNull(eh.findWageByName(emp, null));

  }

  // misc tests
  @Test
  public void testWageEmployeeField() {
    final EmployeeWage wage = new EmployeeWage.Builder("Regular", BigDecimal.TEN).employee(emp)
        .build();
    assertTrue(eh.addNewEmployeeWage(wage));
    for (final EmployeeWage empWage : emp.getWages()) {
      assertEquals(emp, empWage.getEmployee());
    }
  }

  @Test
  public void testEmployeeWageEqualsEmployeeWageStackOverflow() {
    final EmployeeWage employeeWage = new EmployeeWage.Builder("Regular", BigDecimal.TEN).employee(
        emp).build();
    assertTrue(eh.addNewEmployeeWage(employeeWage));
    assertNotNull(emp.getWage(employeeWage.getName()));
    assertEquals(employeeWage, emp.getWage(employeeWage.getName()));
  }
}
