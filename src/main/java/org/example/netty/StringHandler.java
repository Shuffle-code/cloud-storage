package org.example.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
// не смог скачать мавен lombok. Скачал, установил.
@Slf4j

public class StringHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected ...");
        System.out.println("Client connected ...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected ...");
        System.out.println("Client disconnected ...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Handler error: ", cause);
        System.out.println("Handler error: " + cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        ctx.writeAndFlush(s); // s получили s отдали
        log.info("received: {}",s);
        System.out.println("received: " + s);

    }
}
