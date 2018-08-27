
package cps.core.db.tools;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.DateTime;

public class JodaDateTimeConverter implements Converter {

  private static final long serialVersionUID = 1L;

  @Override
  public Object convertDataValueToObjectValue(Object dataValue, Session session) {
    // format of the converted object after database insertion
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
    try {
      return dataValue == null ? null : new DateTime(sdf.parse(dataValue.toString()));
    } catch (ParseException e) {
      // TODO add error handling
      return new DateTime(dataValue);
    }
  }

  @Override
  public Object convertObjectValueToDataValue(Object objectValue, Session session) {
    return objectValue == null ? null : new Timestamp(((DateTime) objectValue).getMillis());
  }

  @Override
  public void initialize(DatabaseMapping mapping, Session session) {
  }

  @Override
  public boolean isMutable() {
    return false;
  }
}