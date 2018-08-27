
package cps.err.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import org.slf4j.LoggerFactory;

public interface LoggerGenerator {

  public static Logger getLoggerFor(Class<?> clazz, String path) {
    return LoggerGenerator.getLoggerFor(clazz.getCanonicalName(), path);
  }

  /**
   * Creates a Logger object for a given class name, and creates a File to log into on the given
   * path.
   * 
   * @param className
   *          the name of the Class to create a Logger for
   * @param path
   *          the path to write the file into
   * @return a Logger object to write with for any class
   */
  public static Logger getLoggerFor(String className, String path) {
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    PatternLayoutEncoder ple = new PatternLayoutEncoder();
    ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
    ple.setContext(lc);
    ple.start();
    FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
    fileAppender.setFile(path);
    fileAppender.setEncoder(ple);
    fileAppender.setContext(lc);
    fileAppender.start();

    ArrayListAppender errHandlerAppender = new ArrayListAppender();
    errHandlerAppender.setContext(lc);
    errHandlerAppender.start();

    // fa.setThreshold(ch.qos.logback.classic.Level.DEBUG);
    // add appender to any Logger (here is root)
    Logger logger = (Logger) LoggerFactory.getLogger(className);
    logger.addAppender(fileAppender);
    logger.addAppender(errHandlerAppender);
    // logger.setLevel(Level.DEBUG);
    logger.setAdditive(false); /* set to true if root should log too */
    return logger;
  }

}
