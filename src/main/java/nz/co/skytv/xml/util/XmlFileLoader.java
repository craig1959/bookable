package nz.co.skytv.xml.util;

import java.io.File;


public interface XmlFileLoader<T> {

  T parseFile(File file) throws FileLoaderException;

}
