
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Client {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();

        initSocket(socket);

        // 链接到本地20000端口，超时时间3秒，超过则抛出超时异常
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            // 发送接收数据
            // 要传递信息
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        // 释放资源
        socket.close();
        System.out.println("客户端已退出～");

    }

    private static Socket createSocket() throws IOException {

        Socket socket = new Socket();
        // 绑定到本地20001端口
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    private static void initSocket(Socket socket) throws SocketException {
        // 设置读取超时时间为2秒
        socket.setSoTimeout(2000);

        // 是否复用未完全关闭的Socket地址，对于指定bind操作后的套接字有效
        // 为了防止突然断开服务器，然后重新连连不上的状况
        socket.setReuseAddress(true);

        // 是否开启Nagle算法
        socket.setTcpNoDelay(true);

        // 是否需要在长时无数据响应时发送确认数据（类似心跳包），时间大约为2小时
        socket.setKeepAlive(true);

        // 对于close关闭操作行为进行怎样的处理；默认为false，0
        // false、0：默认情况，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        // true、0：关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对方，并无需经过2MSL等待
        // true、200：关闭时最长阻塞200毫秒，随后按第二情况处理
        socket.setSoLinger(true, 20);

        // 是否让紧急数据内敛，默认false；紧急数据通过 socket.sendUrgentData(1);发送
        socket.setOOBInline(true);

        // 设置接收发送缓冲器大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);

        // 设置性能参数：短链接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1, 1, 0);
    }

    private static void todo(Socket client) throws IOException {
        // 得到Socket输出流
        OutputStream outputStream = client.getOutputStream();

        // 得到Socket输入流
        InputStream inputStream = client.getInputStream();

        // 基本数据,作为流传递
        byte[] buffer = new byte[256];
        // 建立ByteBuffer以传递数据（传递数据用byte的数据类型更好，因为占得资源少）
        // warp()是实现将buffer装进bytebuffer的作用
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.put((byte) 126);

        char c = 'a';
        byteBuffer.putChar(c);

        int i = 2323123;
        byteBuffer.putInt(i);

        // 传递布尔值，最后传递的形式是单独1个Byte
        boolean b = true;
        byteBuffer.put(b ? (byte) 1 : (byte) 0);

        long l = 298789739;
        byteBuffer.putLong(l);

        float f = 12.345f;
        byteBuffer.putFloat(f);

        double d = 13.31241248782973;
        byteBuffer.putDouble(d);

        String str_create = "Hello你好！";
        byteBuffer.put(str_create.getBytes());

        // 发送到服务器
        outputStream.write(buffer, 0, byteBuffer.position() + 1);
        // 基本数据流传递结束

        // 接收服务器返回
        int read = inputStream.read(buffer);
        System.out.println("收到数量：" + read);

        // 构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        // 得到Socket输出流，并转换为打印流
        outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        // 得到Socket输入流，并转换为BufferedReader
        inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do{
            String str = input.readLine();
            socketPrintStream.println(str);

            String echo = socketBufferedReader.readLine();
            if("bye".equalsIgnoreCase(echo)){
                flag = false;
            }else{
                System.out.println(echo);
            }
        }while(flag);





        // 资源释放
        outputStream.close();
        inputStream.close();
    }
}
