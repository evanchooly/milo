/**
 * Copyright (C) 2011 Justin Lee <jlee@antwerkz.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.milocontainer.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import static org.jboss.netty.channel.Channels.*;

import org.milocontainer.ServletContainer;
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
