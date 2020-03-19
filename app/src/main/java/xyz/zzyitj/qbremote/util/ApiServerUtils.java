package xyz.zzyitj.qbremote.util;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import xyz.zzyitj.qbremote.api.RetrofitHttpService;
import xyz.zzyitj.qbremote.model.Server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/10 7:45 下午
 * @email zzy.main@gmail.com
 */
public class ApiServerUtils {
    public static Retrofit getTestRetrofit(Server server) {
        if (server.isHttps()) {
            return RetrofitHttpService.createRetrofit(
                    "https://" + server.getHost() + ":" + server.getPort());
        }
        return RetrofitHttpService.createRetrofit(
                "http://" + server.getHost() + ":" + server.getPort());
    }

    public static Retrofit getRetrofit(Server server) {
        if (server.isHttps()) {
            return RetrofitHttpService.getRetrofit(
                    "https://" + server.getHost() + ":" + server.getPort());
        }
        return RetrofitHttpService.getRetrofit(
                "http://" + server.getHost() + ":" + server.getPort());
    }

    public static String getHashes(String[] hashes) {
        StringBuilder h = new StringBuilder();
        if (hashes == null) {
            return h.toString();
        }
        for (int i = 0; i < hashes.length; i++) {
            if (i != hashes.length - 1) {
                h.append(hashes[i]).append("|");
            } else {
                h.append(hashes[i]);
            }
        }
        return h.toString();
    }

    public static Map<String, RequestBody> generateRequestBody(Map<String, Object> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            RequestBody requestBody;
            if (requestDataMap.get(key) instanceof Boolean) {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                        (requestDataMap.get(key) == null || !((Boolean) requestDataMap.get(key))) ? "false" : "true");
            } else if (requestDataMap.get(key) instanceof Number) {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                        (requestDataMap.get(key) == null ? "" : String.valueOf(requestDataMap.get(key))));
            } else if (requestDataMap.get(key) instanceof File) {
                File file = (File) requestDataMap.get(key);
                builder.addFormDataPart(key, file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file));
                requestBody = builder.build();
            } else if (requestDataMap.get(key) instanceof byte[]) {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                        (byte[]) (requestDataMap.get(key) == null ? "" : requestDataMap.get(key)));
            } else {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                        (String) (requestDataMap.get(key) == null ? "" : requestDataMap.get(key)));
            }
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }
}
