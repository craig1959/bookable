package nz.co.skytv.bp.protocol.messaging.send;



public interface MessageSenderLifecycleAware {

  void registerMessageSender(String endpointId, MessageSender messageSender);

  void deregisterMessageSender(String endpointId);

}
