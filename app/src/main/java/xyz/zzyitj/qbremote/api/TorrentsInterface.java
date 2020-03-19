package xyz.zzyitj.qbremote.api;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;
import xyz.zzyitj.qbremote.model.TorrentInfo;

import java.util.List;
import java.util.Map;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 9:01 下午
 * @email zzy.main@gmail.com
 */
public interface TorrentsInterface {
    @GET(value = ApiConst.TORRENTS_INFO)
    Observable<List<TorrentInfo>> info(@Query("filter") String filter,
                                       @Query("category") String category,
                                       @Query("sort") String sort,
                                       @Query("reverse") Boolean reverse,
                                       @Query("limit") Integer limit,
                                       @Query("offset") Integer offset,
                                       @Query("hashes") String hashes);

    @GET(value = ApiConst.TORRENTS_PAUSE)
    Observable<ResponseBody> pause(@Query("hashes") String hashes);

    @GET(value = ApiConst.TORRENTS_RESUME)
    Observable<ResponseBody> resume(@Query("hashes") String hashes);

    @Multipart
    @POST(value = ApiConst.TORRENTS_ADD)
    Observable<ResponseBody> add(@Part MultipartBody.Part torrent);

    @GET(value = ApiConst.TORRENTS_DELETE)
    Observable<ResponseBody> delete(@Query("hashes") String hashes, @Query("deleteFiles") Boolean deleteFiles);

    @FormUrlEncoded
    @POST(value = ApiConst.TORRENTS_LOCATION)
    Observable<ResponseBody> location(@Field("hashes") String hashes, @Field("location") String location);


    @FormUrlEncoded
    @POST(value = ApiConst.TORRENTS_RENAME)
    Observable<ResponseBody> rename(@Field("hash") String hash, @Field("name") String name);
}
