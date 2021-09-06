package xyz.hcworld.one.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import xyz.hcworld.one.common.Config;

import java.util.concurrent.*;

/**
 * @ClassName: OneClientBoot
 * @Author: 张冠诚
 * @Date: 2021/8/31 9:47
 * @Version： 1.0
 */
public class OneClientBoot extends Thread {


    /**
     * 核心线程池大小
     */
    public static final int CORE_POOL_SIZE = 5;
    /**
     * 最大线程池大小
     */
    public static final int MAX_POOL_SIZE = 10;
    /**
     * 阻塞任务队列大小
     */
    public static final int QUEUE_CAPACITY = 100;
    /**
     * 空闲线程存活时间
     */
    public static final Long KEEP_ALIVE_TIME = 1L;

    /**
     * 在reconnect之前 Sleep 5 秒钟
     */
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
    /**
     * 如果在10秒中之内没有任何相应则重连
     */
    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "10"));

    private static final PojoClientHandler POJO_CLIENT_HANDLER = new PojoClientHandler();
    private static final Bootstrap BS = new Bootstrap();


    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            BS.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(Config.HOST, Config.PORT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(// 添加encoder和decoder
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new IdleStateHandler(READ_TIMEOUT, 0, 0),
                                    POJO_CLIENT_HANDLER);
                        }
                    });

            // 连接服务器
            ChannelFuture f = BS.connect().sync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static void connect() {

        BS.connect().addListener(future -> {
            if (future.cause() != null) {
                POJO_CLIENT_HANDLER.startTime = -1;
                POJO_CLIENT_HANDLER.println("建立连接失败: " + future.cause());
            }
        });
    }

    public static void main(String[] args) {


        //通过ThreadPoolExecutor构造函数自定义参数创建
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());


        for (int i = 0; i < 1; i++) {
            executor.execute(new OneClientBoot());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
