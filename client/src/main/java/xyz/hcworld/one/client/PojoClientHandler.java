package xyz.hcworld.one.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;
import xyz.hcworld.one.common.Config;
import xyz.hcworld.one.common.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * 使用对象进行数据传输（客户端）
 *
 * @ClassName: PojoClientHandler
 * @Author: 张冠诚
 * @Date: 2021/9/3 11:17
 * @Version： 1.0
 */
@Sharable
@Log4j2
public class PojoClientHandler extends ChannelInboundHandlerAdapter {
    int i = 0;

    long startTime = -1;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        println("连接到: " + ctx.channel().remoteAddress());
        // 在channel active的时候发送消息
        ChannelFuture future = ctx.writeAndFlush("中国");
        // 将ChannelFuture中的Throwable转发到ChannelPipeline中。
        future.addListener(FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 将消息写回channel
        log.info("客户端收到对象:{}", msg);
        User user = new User();
        user.setUsername("测试账号：" + i);
        user.setPassword("测试密码：" + i);
        i++;
        if (i<=10){
            ctx.writeAndFlush(user);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        log.error("出现异常", cause);
        ctx.close();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        println("连接断开:" + ctx.channel().remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent e = (IdleStateEvent) evt;
        if (e.state() == IdleState.READER_IDLE) {
            // 在Idle状态
            println("Idle状态，关闭连接");
            ctx.close();
        }
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        println("sleep:" + OneClientBoot.RECONNECT_DELAY + 's');
        i=0;
        ctx.channel().eventLoop().schedule(() -> {
            println("重连接: " + Config.HOST + ':' + Config.PORT);
            OneClientBoot.connect();
        }, OneClientBoot.RECONNECT_DELAY, TimeUnit.SECONDS);

    }

    void println(String msg) {
        if (startTime < 0) {
            log.error("服务下线:{}", msg);

        } else {
            log.error("服务运行时间:{},{}", (System.currentTimeMillis() - startTime) / 1000, msg);
        }
    }
}
