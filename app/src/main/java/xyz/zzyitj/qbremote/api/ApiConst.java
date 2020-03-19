package xyz.zzyitj.qbremote.api;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 7:35 下午
 * @email zzy.main@gmail.com
 */
public class ApiConst {
    public static final String API_BASE_URL = "/api/v2";
    private static final String API_AUTH_BASE_URL = "/auth";
    public static final String AUTH_LOGIN = API_BASE_URL + API_AUTH_BASE_URL + "/login";
    public static final String AUTH_LOGOUT = API_BASE_URL + API_AUTH_BASE_URL + "/logout";
    private static final String API_TORRENTS_BASE_URL = "/torrents";
    public static final String TORRENTS_INFO = API_BASE_URL + API_TORRENTS_BASE_URL + "/info";
    public static final String TORRENTS_PAUSE = API_BASE_URL + API_TORRENTS_BASE_URL + "/pause";
    public static final String TORRENTS_RESUME = API_BASE_URL + API_TORRENTS_BASE_URL + "/resume";
    public static final String TORRENTS_ADD = API_BASE_URL + API_TORRENTS_BASE_URL + "/add";
    public static final String TORRENTS_DELETE = API_BASE_URL + API_TORRENTS_BASE_URL + "/delete";
    public static final String TORRENTS_LOCATION = API_BASE_URL + API_TORRENTS_BASE_URL + "/setLocation";
    public static final String TORRENTS_RENAME = API_BASE_URL + API_TORRENTS_BASE_URL + "/rename";
    private static final String API_SYNC_BASE_URL = "/sync";
    public static final String SYNC_MAIN_DATA = API_BASE_URL + API_SYNC_BASE_URL + "/maindata";
}
