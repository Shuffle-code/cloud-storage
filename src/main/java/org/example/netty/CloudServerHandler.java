package org.example.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.model.CloudMessage;
import org.example.model.FileMessage;
import org.example.model.FileRequest;
import org.example.model.ListMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // init client dir
        currentDir = Paths.get("data");
        sendList(ctx);
//        ctx.writeAndFlush(new ListMessage(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getType()){
            case FILE:
                processFileMessage((FileMessage) cloudMessage);
                sendList(ctx);
                break;
            case FILE_REQUEST:
                processFileRequest((FileRequest) cloudMessage, ctx);
                break;
        }

    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(currentDir));
    }

    private void processFileMessage(FileMessage cloudMessage) throws IOException {
        Files.write(currentDir.resolve(cloudMessage.getFileName()), cloudMessage.getBytes());

    }

    private void processFileRequest(FileRequest cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = currentDir.resolve(cloudMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));

    }
}
