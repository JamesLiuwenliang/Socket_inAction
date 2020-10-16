package client;

import client.bean.ServerInfo;

public class Client {
    public static void main(String[] args) {
        // 建立信息类，超时时间设为10s，没有搜索到就会失效
        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("Server:" + info);
    }
}
