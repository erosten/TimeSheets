
package cps.core.db.tools;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

/**
 * This class implements an EclipseLink converter interface, allowing annotation for JodaTime
 * LocalDate classes to be mapped to the database. Currently has a wierd issue where mapped dates
 * are stored one day behind, so returned data values are incremented by one.
 * 
 * @author Erik Rosten, 2017
 *
 */
public class JodaLocalDateConverter implements Converter {

  private static final long serialVersionUID = 1L;
  private static final DateTimeZone jodaTzUTC = DateTimeZone.forID("UTC");

  /**
   * 
   * This method converts a joda time LocalDate to a java.sql.Date
   * <p>
   * From
   * https://www.eclipse.org/eclipselink/api/2.6/org/eclipse/persistence/mappings/converters/Converter.html
   * *
   * <p>
   * Convert the object's representation of the value to the databases' data representation.
   */
  @Override
  public Object convertDataValueToObjectValue(Object dataValue, Session session) {
    // casting dataValue to a string results in ClassCastException
    java.sql.Date dbDate = java.sql.Date.valueOf(dataValue.toString());
    return dataValue == null ? null : new LocalDate(dbDate.getTime(), jodaTzUTC).plusDays(1);
  }

  /**
   * This method converts a java.sql.date to a joda time LocalDate. From
   * https://www.eclipse.org/eclipselink/api/2.6/org/eclipse/persistence/mappings/converters/Converter.html:
   * <p>
   * Convert the databases' data representation of the value to the object's representation
   */
  @Override
  public Object convertObjectValueToDataValue(Object objectValue, Session arg1) {
    return objectValue == null
        ? null
        : new java.sql.Date(
            ((LocalDate) objectValue).toDateTimeAtStartOfDay(jodaTzUTC).getMillis());
  }

  /**
   * From
   * https://www.eclipse.org/eclipselink/api/2.6/org/eclipse/persistence/mappings/converters/Converter.html:
   * 
   * Allow for any initialization. This method is unimplemented and should not be used.
   */
  @Override
  public void initialize(DatabaseMapping arg0, Session arg1) {
    // unimplemented
  }

  /**
   * From
   * https://www.eclipse.org/eclipselink/api/2.6/org/eclipse/persistence/mappings/converters/Converter.html:
   * If the converter converts the value to a mutable value, i.e. a value that can have its' parts
   * changed without being replaced, then it must return true. If the value is not mutable, cannot
   * be changed without replacing the whole value then false must be returned. This is used within
   * the UnitOfWork to determine how to clone. This method returns rue.
   */
  @Override
  public boolean isMutable() {
    return true;
  }

}
