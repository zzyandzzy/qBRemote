package xyz.zzyitj.qbremote.api;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import xyz.zzyitj.qbremote.model.Server;
import xyz.zzyitj.qbremote.util.ApiServerUtils;


/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 6:40 下午
 * @email zzy.main@gmail.com
 */
public class AuthService {
    private static final String TAG = AuthService.class.getSimpleName();

    public static Observable<ResponseBody> test(Server server) {
        AuthInterface request = ApiServerUtils.getTestRetrofit(server).create(AuthInterface.class);
        return request.login(server.getUsername(), server.getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> login(Server server) {
        AuthInterface request = ApiServerUtils.getRetrofit(server).create(AuthInterface.class);
        return request.login(server.getUsername(), server.getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
