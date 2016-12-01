package nz.co.skytv.bp.protocol.cisco;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ITVTSHttpClient implements ITVTSClient {

  private static final Logger log = LoggerFactory.getLogger(ITVTSHttpClient.class);

  @Value("${ITVTSendpoint.property}")
  String url;

  ContentType contentType = ContentType.TEXT_XML;
  String charset = "UTF-8";

  public ITVTSHttpClient() {
  }

  public ITVTSHttpClient(String url) {
    this.url = url;
  }


  @Override
  public HttpResponse post(String content) throws ClientProtocolException, IOException {

    HttpEntity entity = MultipartEntityBuilder.create()
        .addPart("triggerRequest", new CustomContentBody(content, contentType, charset, null))
        .build();

    HttpUriRequest request = RequestBuilder.post()
        .setUri(url)
        .setEntity(entity)
        .build();

    HttpResponse response = HttpClientBuilder.create()
        .build()
        .execute(request);

    if (!response.getStatusLine().toString().contains("HTTP/1.1 200 OK")) {
      log.error("Failed , status = '{}'", response.getStatusLine());
    }

    return response;
  }


  /**
   * The Cisco ITVTS interface is very particular about the Multipart/form-data request.<br>
   */
  protected static class CustomContentBody extends AbstractContentBody {

    private final String content;
    private final String charset;
    private final String filename;

    public CustomContentBody(String content, ContentType contentType, String charset, String filename) {
      super(contentType);

      this.content = content;
      this.charset = charset;
      this.filename = filename;
    }

    @Override
    public String getCharset() {
      return charset;
    }

    @Override
    public long getContentLength() {
      return content.length();
    }

    @Override
    public String getFilename() {
      return filename;
    }

    @Override
    public String getTransferEncoding() {
      return MIME.ENC_BINARY;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
      out.write(content.getBytes(), 0, content.length());
    }
  }


}
