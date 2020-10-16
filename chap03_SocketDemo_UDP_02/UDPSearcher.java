import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Searcher开监听端口30000，Provider有自己的UUID，开的端口是20000。
 * Provider启动后进入等待，Searcher启动后即开端口30000，随后广播发送信息，信息要求回传端口30000。
 * Provider将自己的UUID传给Searcher，Searcher解析成Device打印
 * 
 * 关闭Searcher后重新启动，Provider可以重新接受广播信息再重新回传
*/
public class UDPSearcher {

    private static final int LISTEN_PORT = 30000 ;


    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("UDPSearcher Started.");

        /**
         * 1. 开始监听
         */
        Listener listener = listen();

        /**
         * 2. 发送广播
         */
        sendBroadcast();

        // 读取任意键盘信息后可以进入操作
        System.in.read();

        /**
         * 3. 拿监听到的设备信息
         */
        List<Device> devices = listener.getDevicesAndClose();

        for(Device device : devices){
            System.out.println("Device:"+ device.toString());
        }

        System.out.println("UDPSearcher Finished.");



    }

    private static Listener listen() throws InterruptedException {

        System.out.println("UDPSearcher start listen.");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT,countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;

    }

    private static void  sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast started.");

        // 作为搜索方，系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        //构建一份请求数据
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();

        // 直接根据发送者构建一份请求信息
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);

        // 设置IP地址为广播IP地址,端口还是20000
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(20000);

        ds.send(requestPacket);
        ds.close();

        System.out.println("UDPSearcher sendBroadcast finished.");
    }

    private static class Device{
        final int port;
        final String ip;
        final String sn;


        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    private static class Listener extends Thread {

        private final int listenPort;
        private boolean done = false;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();

        private DatagramSocket ds = null;


        public Listener(int listenPort ,CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run(){
            super.run();

            // 通知已启动
            countDownLatch.countDown();

            try{
                // 监听回送端口
                ds = new DatagramSocket(listenPort);

                while(!done){

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
                    System.out.println("UDPProvider receive from ip :"+ip + "\tPort："+port +"\tdata:"+data);

                    // 解析操作
                    String sn = MessageCreator.parseSN(data);
                    if(sn!=null){
                        Device device = new Device(port ,ip ,sn);
                        devices.add(device);
                    }

                }
            }catch (Exception ignored ){

            }finally {
                close();
            }
            System.out.println("UDPProvider listener Finished");

        }

        private void close(){
            if(ds!=null){
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose(){
            done = true;
            close();
            return devices;
        }
    }
}
