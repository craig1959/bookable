package nz.co.skytv.bp.protocol.cisco;

import java.io.IOException;
import org.apache.http.HttpResponse;


/**
 * Interface for accessing the XTI API of Cisco StreamServer.<br>
 * 
 */
public interface ITVTSClient {

  public HttpResponse post(String content) throws IOException;

}
