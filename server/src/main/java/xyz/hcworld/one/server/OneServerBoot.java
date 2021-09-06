package xyz.hcworld.one.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import xyz.hcworld.one.common.Config;

/**
 * @ClassName: OneServerBoot
 * @Author: 张冠诚
 * @Date: 2021/8/31 9:38
 * @Version： 1.0
 */
public class OneServerBoot {


    public static void main(String[] args) {
        //建立两个EventloopGroup用来处理连接和消息
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(Config.PORT)
                    // tcp/ip协议listen函数中的backlog参数,等待连接池的大小
                    .option(ChannelOption.SO_BACKLOG, 100)
                    //日志处理器
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(new LoggingHandler(LogLevel.INFO));
//                            p.addLast(new FirstServerHandler());
//                            p.addLast(new FirstServerHandler());
                            p.addLast(// 添加encoder和decoder
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new PojoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口并开始接收连接
            ChannelFuture f = b.bind().sync();
            // 等待所有的socket都关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
