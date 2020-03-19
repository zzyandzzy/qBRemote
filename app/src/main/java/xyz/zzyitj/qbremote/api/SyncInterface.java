package xyz.zzyitj.qbremote.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import xyz.zzyitj.qbremote.model.SyncMainData;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/11 5:40 下午
 * @email zzy.main@gmail.com
 */
public interface SyncInterface {
    @GET(value = ApiConst.SYNC_MAIN_DATA)
    Observable<SyncMainData> mainData(@Query("rid") Integer rid);
}
