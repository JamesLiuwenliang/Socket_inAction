import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

public class UDPProvider {
    public static void main(String[] args) throws IOException {

        // 生成一份唯一标示
        String sn = UUID.randomUUID().toString();

        Provider provider = new Provider(sn);
        provider.start();

        // 输入任一字符，默认线程结束
        System.in.read();

        provider.exit();

    }

    private static class Provider extends Thread{
        private final String sn;
        // 是否完成的状态标志
        private boolean done = false;
        private DatagramSocket ds = null;
        public Provider(String sn){
            super();
            this.sn = sn;
        }

        @Override
        public void run(){
            super.run();

            System.out.println("UDPProvider started.");
            try {

                // 构建一个监听,监听50000端口
                ds = new DatagramSocket(20000);
                while (!done) {

                    // 构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

                    // 接收
                    ds.receive(receivePack);

                    // 打印接收到的信息与发送者的信息
                    // 发送者的IP地址
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();

                    String data = new String(receivePack.getData(), 0, dataLen);
                    System.out.println("UDPProvider receive from ip :" + ip + "\tPort：" + port + "\tdata:" + data);

                    // 解析端口号
                    int responsePort = MessageCreator.parsePort(data);

                    // 如果不是-1,就说明成功解析端口号，进行回送
                    if(responsePort != -1) {
                        //构建回送数据
                        String responseData = MessageCreator.buildWithSN(sn);

                        byte[] responseDataBytes = responseData.getBytes();

                        // 直接根据发送者构建一份回送信息
                        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                                responseDataBytes.length,
                                receivePack.getAddress(),
                                responsePort);
                        ds.send(responsePacket);
                    }
                }
            }catch (Exception ignored){
            }finally{
                close();
            }
            System.out.println("UDPProvider close.");

        }

        private void close(){
            if(ds != null){
                ds.close();
                ds = null;
            }
        }

        /**
         * 提供结束的方法
         */
        void exit(){
            done = true;
            close();
        }
    }
}
