package org.milo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.proxy.AbstractProxyIoHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServletContainer extends ServletContainer {
    private DefaultIoFilterChainBuilder chain;
    private IoAcceptor acceptor;

    public MinaServletContainer(int port) {
        this("localhost", port);
    }

    public MinaServletContainer(String host, int port) {
        super(host, port);
        acceptor = new NioSocketAcceptor();
        acceptor.setHandler(new AbstractProxyIoHandler() {
            @Override
            public void proxySessionOpened(IoSession session) throws Exception {
                System.out.println("session = " + session);
            }

            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                super.messageReceived(session, message);
            }
        });
    }

    public void enableSSL() {
        try {
            SslFilter sslFilter = new SslFilter(SSLContext.getDefault());
            chain.addLast("sslFilter", sslFilter);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void start() throws IOException {
        acceptor.bind(new InetSocketAddress(host, port));
    }

    @Override
    public void stop() {
        acceptor.unbind();
    }

    @Override
    public String getMimeType(String file) {
        return null;
    }
}