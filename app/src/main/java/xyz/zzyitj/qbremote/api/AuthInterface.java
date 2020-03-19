package xyz.zzyitj.qbremote.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 8:03 下午
 * @email zzy.main@gmail.com
 */
public interface AuthInterface {
    @GET(value = ApiConst.AUTH_LOGIN)
    Observable<ResponseBody> login(@Query("username") String username, @Query("password") String password);
}
