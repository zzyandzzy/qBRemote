package xyz.zzyitj.qbremote.api;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import xyz.zzyitj.qbremote.model.Server;
import xyz.zzyitj.qbremote.model.SyncMainData;
import xyz.zzyitj.qbremote.util.ApiServerUtils;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/11 5:39 下午
 * @email zzy.main@gmail.com
 */
public class SyncService {
    public static Observable<SyncMainData> mainData(Server server, int rid) {
        SyncInterface request = ApiServerUtils.getRetrofit(server).create(SyncInterface.class);
        return request.mainData(rid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
