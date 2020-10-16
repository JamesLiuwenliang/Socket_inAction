package server;

import constants.TCPConstants;

import java.io.IOException;

    /*
    *  实现过程： Server端启动后开启端口（Port：30401）监听
    *           Client端开启后，开启自己的监听端口（Port：30202），并广播（Port：64738，随机分配），
    *           Server端（Port：30201）回送消息到Client端（Port：30202）
    *
    * */
public class Server {
    public static void main(String[] args) {

        // TCPConstants.PORT_SERVER 是服务器的端口，客户端都不知道，所以客户端需要先广播，然后服务器将自己的端口回送
        // 该线程做的就是编辑服务器回送客户端的信息
        ServerProvider.start(TCPConstants.PORT_SERVER);

        try {
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerProvider.stop();
    }
}
