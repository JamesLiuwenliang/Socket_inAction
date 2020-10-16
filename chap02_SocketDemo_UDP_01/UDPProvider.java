import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
// UDP 包最大的有效长度是65507byte

public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider started.");

        // 作为接收者，指定一个端口用于数据接收
        // 用于接收与发送UDP的类，负责发送某一个UDP包或者接受UDP包
        // DatagramSocket(int port); 创建监听固定端口的实例，但不指定端口和IP
        // DatagramSocket(int port, Inet Address localAddr); 创建固定端口指定
        DatagramSocket ds = new DatagramSocket(50000);

        // 构建接收实体
        // DatagramPacket(byte[] buf,int offset,int length,InetAddress address ,int port);
        // 前三个参数指定buff的使用区间，后面两个参数指定目标机器地址与端口
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
        System.out.println("UDPProvider receive from ip :"+ip + "\tPort："+port +"\tdata:"+data);

        //构建回送数据
        String responseData = "Receive data with len:"+dataLen;
        byte[] responseDataBytes = responseData.getBytes();

        // 直接根据发送者构建一份回送信息
        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                responseDataBytes.length,
                receivePack.getAddress(),
                receivePack.getPort());

        ds.send(responsePacket);
        System.out.println("UDPProvider close.");
        ds.close();



    }
}
