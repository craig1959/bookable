package nz.co.skytv.bp.protocol.config;

import java.io.File;
import javax.xml.bind.JAXBException;
import nz.co.skytv.bp.protocol.model.ConfigType;
import nz.co.skytv.xml.util.JAXBXmlFileLoader;
import org.springframework.stereotype.Component;


/**
 * ConfigXmlFileClassLoader uses JAX-B to marshall the endpoints in config.xml file into<br>
 * Java {@link ConfigType}
 */
@Component
public class ConfigXmlFileClassLoader extends JAXBXmlFileLoader<ConfigType> {

  @Override
  public ConfigType parseFile(File file) throws ConfigFileLoaderException {

    try {
      return parseFile(file, ConfigType.class);
    } catch (JAXBException e) {
      throw new ConfigFileLoaderException("Error occurred while loading bookablepromos configuration, exception %s", e);
    }

  }

}
