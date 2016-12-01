package nz.co.skytv.bp.protocol.socket.common;

import io.netty.channel.ChannelInboundHandler;


public interface ChannelInboundHandlerFactory {

  ChannelInboundHandler createChannelInboundHandler();

}
