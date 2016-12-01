package nz.co.skytv.bp.protocol.socket.common;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;


public class SocketChannelInitialiser extends ChannelInitializer<SocketChannel> {

  private ChannelInboundHandlerFactory handlerFactory;

  public SocketChannelInitialiser(ChannelInboundHandlerFactory handlerFactory) {
    this.handlerFactory = handlerFactory;
  }

  @Override
  protected void initChannel(SocketChannel channel) throws Exception {
    ChannelPipeline pipeline = channel.pipeline();
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter()));
    pipeline.addLast("decoder", new ByteArrayDecoder());
    pipeline.addLast("encoder", new ByteArrayEncoder());
    pipeline.addLast("handler", handlerFactory.createChannelInboundHandler());
  }

}
