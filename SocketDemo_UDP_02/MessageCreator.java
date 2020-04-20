public class MessageCreator {

    private static final String SN_HEADER = "收到暗号，我是（SN）：";
    private static final String PORT_HEADER = "这是暗号，请回送端口（Port）:";

    // 创建信息
    public static String buildWithPort (int port){
        return PORT_HEADER + port;
    }

    // 解析方法,解析成一个Integer值
    public static int parsePort(String data){

        if(data.startsWith(PORT_HEADER)){
            return Integer.parseInt(data.substring(PORT_HEADER.length()));
        }
        return -1;
    }

    // SN 创建信息方法
    public static String buildWithSN(String sn){
        return SN_HEADER + sn;
    }

    // SN 解析信息
    public static String parseSN (String data){
        if(data.startsWith(SN_HEADER)){
            return data.substring(SN_HEADER.length());
        }
        return null;
    }






}
