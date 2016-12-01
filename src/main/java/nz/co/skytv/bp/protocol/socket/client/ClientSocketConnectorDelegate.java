package nz.co.skytv.bp.protocol.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import nz.co.skytv.bp.protocol.messaging.MessageHandler;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.common.ChannelInboundHandlerFactory;
import nz.co.skytv.bp.protocol.socket.common.SocketChannelInitialiser;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientSocketConnectorDelegate implements SocketConnector, ChannelInboundHandlerFactory {

  private static final Logger log = LoggerFactory.getLogger(ClientSocketConnectorDelegate.class);

  volatile Channel channel;
  private EventLoopGroup workerGroup;
  private MessageHandler messageHandler;
  private int port;
  private String host;
  private boolean autoconnect;
  private int reinitialiseDelay;
  private volatile boolean stopped = false;

  public ClientSocketConnectorDelegate(EndpointType endpoint, MessageHandler messageHandler) {
    this.host = endpoint.getHost();
    this.port = endpoint.getPort();
    this.messageHandler = messageHandler;
  }

  public void setReconectDelay(int reinitialiseDelay) {
    this.reinitialiseDelay = reinitialiseDelay;
  }

  public void setAutoconnect(boolean autoconnect) {
    this.autoconnect = autoconnect;
  }

  @Override
  public void start() throws InterruptedException {
    log.info("Starting client socket on port '{}'", port);
    if (!stopped) {
      openSocket();
    }
  }

  @Override
  public void stop() {
    stopped = true;
    stopChannel();
  }

  private void stopChannel() {
    log.info("Stopping client socket on port '{}'", port);

    try {
      if (channel != null && channel.isOpen()) {
        channel.close().sync();
        channel = null;
      }
      log.trace("client Socket on port '{}' stopped.", port);
    } catch (InterruptedException e) {
      log.error("Failed to close client socket on port '{}'", port, e);
    } finally {
      if (workerGroup != null) {
        Future<?> fw = workerGroup.shutdownGracefully();
        try {
          // when shutting down in tomcat waits for the netty threads to die
          fw.await(30000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        workerGroup = null;
        log.trace("client workerGroup has stopped successfully on port '{}'", port);
      }
    }
  }


  protected void reinitialiseChannel() {
    stopChannel();
    if (!stopped) {
      try {
        Thread.sleep(reinitialiseDelay * 1000);
        start();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  Bootstrap bootstrap;
  ClientChannelFutureListener channelListener;

  private void openSocket() throws InterruptedException {
    workerGroup = new NioEventLoopGroup();
    bootstrap = new Bootstrap();
    bootstrap.group(workerGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(new SocketChannelInitialiser(this));

    channelListener = new ClientChannelFutureListener();
    bootstrap.connect(this.host, this.port)
        .addListener(channelListener);

  }

  public void sendMessage(byte[] responseMessage) {
    if (responseMessage == null) {
      throw new IllegalArgumentException("Response Message should not be null");
    }
    channel.write(responseMessage);
    channel.flush();
  }

  @Override
  public ChannelInboundHandler createChannelInboundHandler() {
    return new ClientChannelInboundHandler();
  }

  private class ClientChannelInboundHandler extends SimpleChannelInboundHandler<byte[]> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] byteMessage) throws Exception {
      messageHandler.handleMessage(byteMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      log.trace("client socket exception on port '{}'", port, cause);
      reinitialiseChannel();//if socket exception try to reconnect
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      log.trace("client socket channelInactive on port '{}'", port);
      reinitialiseChannel();//if remote automation goes down try to reconnect
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      log.trace("client socket channelUnregistered on port '{}'", port);
    }

  }

  private class ClientChannelFutureListener implements ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
      if (future.isSuccess()) {
        channel = future.channel();
        log.info("client socket connected on port '{}'", port);
      } else {
        log.debug(" No socket to connect to. waiting.....");
        if (autoconnect) {
          reinitialiseChannel();
        }
      }

    }
  }


}
