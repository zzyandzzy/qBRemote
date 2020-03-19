package xyz.zzyitj.qbremote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/10 5:04 下午
 * @email zzy.main@gmail.com
 */
@Getter
@Setter
@ToString
public class Server {
//    public static final String API_BASE_URL = "/api/v2/";

    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean isHttps = false;
//    private String rpcUrl = API_BASE_URL;
}
