package nz.co.skytv.bp.protocol.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import java.util.Collection;
import java.util.HashSet;
import nz.co.skytv.bp.protocol.messaging.send.MessageSender;
import nz.co.skytv.bp.protocol.messaging.send.MessageSenderLifecycleAware;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.common.ChannelInboundHandlerFactory;
import nz.co.skytv.bp.protocol.socket.common.SocketChannelInitialiser;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * ServerSocketConnector give socket connectivity to NDS automation system socket.
 */
public class ServerSocketConnector implements SocketConnector, ChannelInboundHandlerFactory, MessageSender {

  private static final Logger log = LoggerFactory.getLogger(ServerSocketConnector.class);

  @Autowired
  MessageSenderLifecycleAware sendMessageService;

  private Collection<Channel> channels = new HashSet<>();
  private EventLoopGroup workerGroup;
  private EventLoopGroup connectionGroup;
  private int port;
  private String endpointId;
  private Channel serverChannel;
  private volatile boolean stopped = false;

  public ServerSocketConnector() {
  }

  public ServerSocketConnector(EndpointType endpoint) {
    this.port = endpoint.getPort();
    this.endpointId = endpoint.getId();
    log.debug("instantiating server {} on port {}", endpointId, port);
  }

  @Override
  public void start() {

    log.debug("Starting server {} on port {}", endpointId, port);
    sendMessageService.registerMessageSender(endpointId, this);
    try {
      connect();
    } catch (InterruptedException e) {
      log.error("Bridge Server Exception connecting with NDS on port '{}'", port, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void stop() {
    stopped = true;
    stopChannel();
  }

  private void stopChannel() {
    log.trace("Stopping server socket on port '{}'", port);
    sendMessageService.deregisterMessageSender(endpointId);
    try {
      for (Channel channel : channels) {
        if (channel != null) {
          channel.close();
        }
      }
      serverChannel.close().sync();
      log.trace("Server socket on port '{}' stopped.", port);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      Future<?> fc = connectionGroup.shutdownGracefully();
      Future<?> fw = workerGroup.shutdownGracefully();
      try {
        fc.await(); // when shutting down in tomcat waits for the netty threads to die
        fw.await();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      log.trace("Server workerGroup has shutdown successfully on port '{}'", port);
    }
  }

  protected void reinitialiseChannel() {
    stopChannel();
    if (!stopped) {
      try {
        start();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void sendMessage(byte[] message) {

    if (!channels.isEmpty()) {
      log.info("sending bridge request: {} for endpoint {}", new String(message), endpointId);

      for (Channel channel : channels) {
        channel.write(message);
        channel.flush();
      }
    } else {
      log.error("failed to send bridge request, no NDS server connected for endpoint {}", endpointId);
    }

  }

  private void connect() throws InterruptedException {
    connectionGroup = new NioEventLoopGroup();
    workerGroup = new NioEventLoopGroup();

    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(connectionGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(new SocketChannelInitialiser(this));

    serverChannel = bootstrap.bind(port).sync().channel();
  }

  @Override
  public ChannelInboundHandler createChannelInboundHandler() {
    return new ServerChannelInboundHandler();
  }

  private class ServerChannelInboundHandler extends SimpleChannelInboundHandler<byte[]> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
      // NDS sends a heart beat to keep alive the automation systems.
      // this is being  handled by the client servers.
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

      Channel channel = ctx.channel();
      if (channels.add(channel)) {
        log.info("added client connection on port '{}' address '{}'", port, channel.remoteAddress());
      }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();
      if (channels.remove(channel)) {
        log.info("removed client connection on port '{}' address '{}'", port, channel.remoteAddress());
      } else {
        log.error("failed to remove client connection on port '{}' ", port);
      }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      log.debug("error on NDS server port '{}' ", port, cause);
      reinitialiseChannel();
    }

  }

}
