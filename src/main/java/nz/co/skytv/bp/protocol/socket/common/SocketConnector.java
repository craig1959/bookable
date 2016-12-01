package nz.co.skytv.bp.protocol.socket.common;



public interface SocketConnector {

  void start() throws InterruptedException;

  void stop();

}
