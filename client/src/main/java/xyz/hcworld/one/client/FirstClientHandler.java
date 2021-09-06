package xyz.hcworld.one.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * 使用流进行数据传输（客户端）
 * @ClassName: FirstClientHandler
 * @Author: 张冠诚
 * @Date: 2021/8/31 9:47
 * @Version： 1.0
 */
@Log4j2
public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf content;
    private ChannelHandlerContext ctx;

    /**
     * 客户端处理器
     */
    public FirstClientHandler() {
        content = Unpooled.buffer(20);

    }

    /**
     * 通道建立时
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        content.writeBytes("中国发送请求".getBytes(StandardCharsets.UTF_8));

        content.retain();
        ctx.writeAndFlush(content);
        content.writeBytes("".getBytes(StandardCharsets.UTF_8));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到消息时
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //清空content的内容
        content.clear();
        content.writeBytes("zxcv".getBytes(StandardCharsets.UTF_8));
        log.info("客户端收到消息:{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
        log.info("可读字节:{},readerIndex:{}",content.readableBytes(),content.readerIndex());
        log.info("可写字节:{},writerIndex:{}",content.writableBytes(),content.writerIndex());
        log.info("capacity:{},refCnt{}",content.capacity(),content.refCnt());

        log.info("可读字节:{},readerIndex:{}",content.readableBytes(),content.readerIndex());
        log.info("可写字节:{},writerIndex:{}",content.writableBytes(),content.writerIndex());
//        content = Unpooled.buffer(ChinaClient.SIZE);

        content.retain();

        ctx.writeAndFlush(content);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        log.error("出现异常", cause);
        ctx.close();
    }

}

