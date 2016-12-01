package nz.co.skytv.bp.protocol.endpoint.lifecycle;


@SuppressWarnings("serial")
public class EndpointLifeCycleException extends Exception {


  public EndpointLifeCycleException(String errorMessage) {
    super(errorMessage);
  }
}
