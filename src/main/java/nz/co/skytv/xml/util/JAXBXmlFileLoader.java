package nz.co.skytv.xml.util;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;


public abstract class JAXBXmlFileLoader<T> implements XmlFileLoader<T> {

  protected T parseFile(File XMLfile, Class<T> clazz) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    JAXBElement<T> root = jaxbUnmarshaller.unmarshal(new StreamSource(XMLfile), clazz);
    return root.getValue();
  }

}
