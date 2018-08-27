
package cps.core.db.tools;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.LocalTime;

/**
 * This class implements an EclipseLink converter interface, allowing annotation for JodaTime
 * LocalTime classes to be mapped to the database.
 * 
 * @author Erik Rosten, 2017
 *
 */
public class JodaLocalTimeConverter implements Converter {

  private static final long serialVersionUID = 1L;

  /**
   * 
   * This method converts a joda time LocalTime to a java.sql.Time. It uses deprecated java.sql.Time
   * methods for conversion.
   * <p>
   * From
   * https://www.eclipse.org/eclipselink/api/2.6/org/eclipse/persistence/mappings/converters/Converter.html
   * *
   * <p>
   * Convert the object's representation of the value to the databases' data representation.
   */
  @SuppressWarnings("deprecation")
  @Override
  public Object convertDataValueToObjectValue(Object dataValue, Session session) {
    // casting dataValue to a string results in ClassCastException
    java.sql.Time time = java.sql.Time.valueOf(dataValue.toString());
    return dataValue == null
        ? null
        : new LocalTime(time.getHours(), time.getMinutes(), time.getSeconds());
  }

  /**
   * This method converts a java.sql.Time to a joda time LocalTime. It uses deprecated java.sql.Time
   * methods for conversion. From
   * https://www.eclipse.org/eclipselink/api/2.6/org/eclipse/persistence/mappings/converters/Converter.html:
   * <p>
   * Convert the databases' data representation of the value to the object's representation
   */
  @SuppressWarnings("deprecation")
  @Override
  public Object convertObjectValueToDataValue(Object objectValue, Session arg1) {
    LocalTime localTime = (LocalTime) objectValue;
    return objectValue == null
        ? null
        : new java.sql.Time(
            localTime.getHourOfDay(),
            localTime.getMinuteOfHour(),
            localTime.getSecondOfMinute());
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

}
