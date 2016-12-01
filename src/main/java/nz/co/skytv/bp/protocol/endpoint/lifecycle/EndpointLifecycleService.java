package nz.co.skytv.bp.protocol.endpoint.lifecycle;

import nz.co.skytv.bp.protocol.model.EndpointType;


public interface EndpointLifecycleService {

  /**
   * create a new socket endpoint from configuration current endpoints.
   * @param endpointType
   * @throws InterruptedException
   */
  void initiateEndpoint(EndpointType endpointType) throws InterruptedException;


  /**
   * destroy socket endpoint, no longer needed.
   * @param endpoint
   * @throws EndpointLifeCycleException
   */
  void destroyEndpoint(EndpointType endpoint) throws EndpointLifeCycleException;


  /**
   * reconfigure an existing endpoint from configuration current endpoints.
   * 
   * @param endpoint
   * @throws EndpointLifeCycleException
   * @throws InterruptedException
   */
  void reconfigureEndpoint(EndpointType endpoint) throws EndpointLifeCycleException, InterruptedException;

}
