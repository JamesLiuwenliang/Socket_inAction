import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSearcher {

    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher started.");

        // 作为搜索方，系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        //构建发送数据
        String requestData = "Hello World.";
        byte[] requestDataBytes = requestData.getBytes();

        // 直接根据发送者构建一份请求信息
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);

        // 设置本机20000端口
        requestPacket.setAddress(InetAddress.getLocalHost());
        requestPacket.setPort(50000);

        ds.send(requestPacket);

        // 构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf ,buf.length);

        // 接收
        ds.receive(receivePack);

        // 打印接收到的信息与发送者的信息
        // 发送者的IP地址
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int dataLen = receivePack.getLength();

        String data = new String(receivePack.getData(),0,dataLen);
        System.out.println("UDPSearcher receive from ip :"+ip + "\tPort："+port +"\tdata:"+data);


        System.out.println("UDPSearcher close.");
        ds.close();

    }
}
