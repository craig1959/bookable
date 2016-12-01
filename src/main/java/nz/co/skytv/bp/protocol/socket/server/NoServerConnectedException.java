package nz.co.skytv.bp.protocol.socket.server;

import nz.co.skytv.bp.protocol.common.exception.BaseRuntimeException;


public class NoServerConnectedException extends BaseRuntimeException {

  private static final long serialVersionUID = -4215995541594389597L;

  public NoServerConnectedException(String message) {
    super("102", message, null);
  }
}
