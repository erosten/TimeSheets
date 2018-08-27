
package cps.err.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class ArrayListAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  @Override
  protected void append(ILoggingEvent eventObject) {
    if (eventObject.getLevel().isGreaterOrEqual(Level.WARN)) {
      ErrorHandler.addError(eventObject.getMessage());
    }
    ErrorHandler.log(eventObject.getMessage());
  }
}