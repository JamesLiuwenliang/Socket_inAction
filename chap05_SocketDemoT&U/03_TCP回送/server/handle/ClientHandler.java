package server.handle;

import client.Client;
import clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
*  ClientHandler 下面包含两个类，分别用来对读取的数据处理，和处理要发送的数据
* */
public class ClientHandler {

    private final Socket socket;
    private final ClientReaderHandler readerHandler;
    private final ClientWriteHandler writerHandler;
    // 这是干啥的
    private final CloseNotify closeNotify;

    public ClientHandler(Socket socket, CloseNotify closeNotify) throws IOException {
        this.socket = socket;
        this.readerHandler = new ClientReaderHandler(socket.getInputStream());
        this.writerHandler = new ClientWriteHandler(socket.getOutputStream());
        this.closeNotify = closeNotify;
        System.out.println("新客户端连接：" + socket.getInetAddress() +
            " P:" + socket.getPort());
    }

    public void exit() throws IOException {
        readerHandler.exit();
        CloseUtils.close(socket);
        System.out.println("客户端已退出：" + socket.getInetAddress() +
                " P:" + socket.getPort());

    }

    public void send(String str){
        writerHandler.send(str);
    }

    public void readToPrint(){
        readerHandler.start();
    }

    private void exitByMyself() throws IOException {
        exit();
        closeNotify.oneSelfClosed(this);
    }

    //
    public interface CloseNotify{
        void oneSelfClosed(ClientHandler handler);
    }


    // 对客户端来的信息进行处理，只处理输入数据
    class ClientReaderHandler extends Thread{

        private boolean done = false;
        private final InputStream inputStream;

        ClientReaderHandler(InputStream inputStream){
            this.inputStream = inputStream;
        }


        @Override
        public void run() {
            super.run();

            try {

                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine();

                    if(str == null){
                        System.out.println("客户端已无法读取数据");
                        // 退出客户端
                        ClientHandler.this.exitByMyself();
                        break;
                    }

                    // 打印到屏幕。并回送数据长度
                    System.out.println(str);


                } while (!done);

            } catch (Exception e) {
                if(!done){
                    System.out.println("连接异常断开");
                    try {
                        ClientHandler.this.exitByMyself();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

            } finally {
                // 连接关闭
                try {
                    CloseUtils.close(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }

        void exit() throws IOException {
            done = true;
            CloseUtils.close(inputStream);
        }


    }


    // 如果是继承extends Thread，那么run起来之后会经常进入阻塞态等状态，可以用线程池实现
    class ClientWriteHandler{
        private boolean done = false;
        private final PrintStream printStream;

        // 利用线程池实现
        private final ExecutorService executorService;

        ClientWriteHandler(OutputStream outputStream){
            this.printStream = new PrintStream(outputStream);
            // 单线程池
            this.executorService = Executors.newSingleThreadExecutor();
        }

        void exit() throws IOException {
            done = true;
            CloseUtils.close(printStream);
            // 关闭线程池
            executorService.shutdownNow();
        }

        void send(String str){
            // 往线程池里丢一个任务进去
            executorService.execute(new WriteRunnable(str));
        }

        class WriteRunnable implements Runnable{
            private final String msg;

            WriteRunnable(String str){
                this.msg = str;
            }

            @Override
            public void run (){

                // 避免线程已经是退出的状态
                if(ClientWriteHandler.this.done){
                    return ;
                }

                try{
                    ClientWriteHandler.this.printStream.println(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }



        }

    }



}
