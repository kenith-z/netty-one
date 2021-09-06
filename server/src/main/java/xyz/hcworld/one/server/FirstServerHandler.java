package xyz.hcworld.one.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;

/**
 * 使用流进行数据传输(服务器)
 * @ClassName: FirstServerHandler
 * @Author: 张冠诚
 * @Date: 2021/8/31 9:37
 * @Version： 1.0
 */
@Log4j2
public class FirstServerHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf content;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 对消息进行处理
        ByteBuf in = (ByteBuf) msg;

        try {
            log.info("收到消息:{}",in.toString(CharsetUtil.UTF_8));
            content = in.retain();
            content.writeBytes("加油".getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(content.retain());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        log.error("出现异常",cause);
        ctx.close();
    }
}

