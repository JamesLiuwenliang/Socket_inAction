package server;

import constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
*  TCP连接建立后，Client端可以发送信息给Server端，Server端回送数据长度
*  进行改造： 利用多线程结构，server端也可以直接发信息给Client端
*  Server端是两个线程，但是Client端是一个线程接收到键盘输入的时候会阻塞住输出
*  但是Server端会有两个线程，当接收到Client端的输入时会丢到该客户端的线程池中
*
* */
public class Server {
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);


//         原来是server端接收到键盘输入后退出
//        try {
//            //noinspection ResultOfMethodCallIgnored
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        do {
            str = bufferedReader.readLine();
            tcpServer.broadcast(str);

        }while(!"bye".equalsIgnoreCase(str));



        UDPProvider.stop();
        tcpServer.stop();
    }
}
