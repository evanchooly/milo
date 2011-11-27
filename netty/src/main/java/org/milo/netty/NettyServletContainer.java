package org.milo.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import static org.jboss.netty.channel.Channels.*;

import org.milo.ServletContainer;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpServerCodec;

public class NettyServletContainer extends ServletContainer {
    private ServerBootstrap bootstrap;
    private String host;
    private int port;
    private Channel channel;

    public NettyServletContainer() {
        this("localhost", 8080);
    }

    public NettyServletContainer(int port) {
        this("localhost", port);
    }

    public NettyServletContainer(String host, final int port) {
        super(host, port);
        this.host = host;
        this.port = port;
        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("deflater", new HttpContentCompressor());
                pipeline.addLast("handler", new HttpServerCodec());
                return pipeline;
            }
        });
    }

    public void enableSsl() {
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addFirst("ssl", new SslHandler(engine));
    }

    @Override
    public void start() throws IOException {
        channel = bootstrap.bind(new InetSocketAddress(host, port));
    }

    @Override
    public void stop() {
        try {
            channel.close().await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String getMimeType(String file) {
        return null;
    }
}
