package xyz.hcworld.one.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;
import xyz.hcworld.one.common.User;
import io.netty.channel.ChannelHandler.Sharable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * 使用对象进行数据传输(服务器)
 * @ClassName: PojoServerHandler
 * @Author: 张冠诚
 * @Date: 2021/9/3 11:13
 * @Version： 1.0
 */
@Sharable
@Log4j2
public class PojoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String) {
            log.info("server收到String对象:{}", msg);
        } else {
            User user = (User) msg;
            log.info("server收到User对象:{}", user.toString());
        }
        ctx.write("加油！");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        log.error("出现异常", cause);
        ctx.close();
    }
}