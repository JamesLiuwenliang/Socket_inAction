package clink.net.qiujuer.clink.utils;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {

    public static void close(Closeable...closeables) throws IOException {
        if(closeables == null){
            return ;
        }

        for(Closeable closeable : closeables){
            closeable.close();
        }

    }

}
