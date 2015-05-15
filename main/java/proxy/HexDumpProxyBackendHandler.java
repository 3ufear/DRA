package com.deltasolutions.dra.tcp;

import io.netty.bootstrap.ChannelFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server {
    public Server(int port) throws Exception{
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()); //Создаем фабрику каналов. Так надо :)

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new NettyServerPipeLineFactory());	//Кастомный класс имплементирующий ChannelPipelineFactory

        bootstrap.setOption("child.tcpNoDelay", true); //Не очень понятная опция, но вроде бы нужна для поддержки постоянного соединения с клиентом
        bootstrap.setOption("child.keepAlive", true); //Аналогично

        bootstrap.bind(new InetSocketAddress(port));	//Вешаем слушатель на переданный в параметрах порт

        System.out.print("NettyServer: Listen to users on "+InetAddress.getLocalHost().toString()+":"+port+"\n");
    }
}