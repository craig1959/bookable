package nz.co.skytv.bp.protocol.config;

import nz.co.skytv.xml.util.FileLoaderException;


/**
 * Exception when Bookable promos config.xml file is loaded and parsed by JAXB.
 * 
 */
@SuppressWarnings("serial")
public class ConfigFileLoaderException extends FileLoaderException {

  public ConfigFileLoaderException(String errorMessage, Throwable cause) {
    super(errorMessage, cause);
  }

}
