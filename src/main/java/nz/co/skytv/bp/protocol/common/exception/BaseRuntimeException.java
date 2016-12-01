package nz.co.skytv.bp.protocol.common.exception;


public abstract class BaseRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 2132591422273488445L;

  private final String errorCode;

  public BaseRuntimeException(String errorCode, String errorMsg, Throwable cause) {
    super(errorMsg, cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
