package nz.co.skytv.bp.protocol.messaging.send;

import nz.co.skytv.bp.protocol.common.exception.BaseRuntimeException;


public class EndpointLocationNotConfiguredException extends BaseRuntimeException {

  private static final long serialVersionUID = 949790128240384913L;

  public EndpointLocationNotConfiguredException(String message) {
    super("101", message, null);
  }

}
