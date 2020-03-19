package xyz.zzyitj.qbremote.api;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import xyz.zzyitj.qbremote.model.vo.AddTorrentsVo;
import xyz.zzyitj.qbremote.model.vo.InfoTorrentsVo;
import xyz.zzyitj.qbremote.model.Server;
import xyz.zzyitj.qbremote.model.TorrentInfo;
import xyz.zzyitj.qbremote.util.ApiServerUtils;

import java.util.List;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 9:02 下午
 * @email zzy.main@gmail.com
 */
public class TorrentsService {
    public static Observable<List<TorrentInfo>> info(Server server) {
        return info(server, new InfoTorrentsVo());
    }

    public static Observable<List<TorrentInfo>> info(Server server, InfoTorrentsVo infoTorrentsVo) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
        return request.info(infoTorrentsVo.getFilter(), infoTorrentsVo.getCategory(), infoTorrentsVo.getSort(),
                infoTorrentsVo.getReverse(), infoTorrentsVo.getLimit(), infoTorrentsVo.getOffset(),
                ApiServerUtils.getHashes(infoTorrentsVo.getHashes()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> pause(Server server, String... hashes) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
        return request.pause(ApiServerUtils.getHashes(hashes))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> resume(Server server, String... hashes) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
        return request.resume(ApiServerUtils.getHashes(hashes))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> delete(Server server, boolean deleteFiles, String... hashes) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
        return request.delete(ApiServerUtils.getHashes(hashes), deleteFiles)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> location(Server server, String location, String... hashes) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
        return request.location(ApiServerUtils.getHashes(hashes), location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> rename(Server server, String name, String hash) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
        return request.rename(hash, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ResponseBody> add(Server server, AddTorrentsVo addTorrentsVo) {
        TorrentsInterface request = ApiServerUtils.getRetrofit(server).create(TorrentsInterface.class);
//        Map<String, Object> map = new HashMap<>();
//        if (addTorrentsVo.getUrls() != null) {
//            map.put("urls", addTorrentsVo.getUrls());
//        } else if (addTorrentsVo.getTorrents() != null) {
//            map.put("torrents", addTorrentsVo.getTorrents());
//        }
//        if (addTorrentsVo.getSavepath() != null) {
//            map.put("savepath", addTorrentsVo.getSavepath());
//        }
//        if (addTorrentsVo.getCookie() != null) {
//            map.put("cookie", addTorrentsVo.getCookie());
//        }
//        if (addTorrentsVo.getCategory() != null) {
//            map.put("category", addTorrentsVo.getCategory());
//        }
//        map.put("skip_checking", addTorrentsVo.getSkip_checking());
//        map.put("paused", addTorrentsVo.getPaused());
//        map.put("root_folder", addTorrentsVo.getRoot_folder());
//        if (addTorrentsVo.getRename() != null) {
//            map.put("rename", addTorrentsVo.getRename());
//        }
//        if (addTorrentsVo.getUpLimit() != null) {
//            map.put("upLimit", addTorrentsVo.getUpLimit());
//        }
//        if (addTorrentsVo.getDlLimit() != null) {
//            map.put("dlLimit", addTorrentsVo.getDlLimit());
//        }
//        map.put("autoTMM", addTorrentsVo.getAutoTMM());
//        map.put("sequentialDownload", addTorrentsVo.getSequentialDownload());
//        map.put("firstLastPiecePrio", addTorrentsVo.getFirstLastPiecePrio());
//        Map<String, RequestBody> map = new HashMap<>();
//        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("torrents", "xxxxx",
//                        RequestBody.create(MediaType.parse("application/octet-stream"), addTorrentsVo.getTorrents()))
//                .build();
        RequestBody requestBody = RequestBody.create
                (MediaType.parse("application/x-bittorrent"),
                        addTorrentsVo.getTorrents());
//        map.put("torrents", body);
        MultipartBody.Part part =
                MultipartBody.Part.createFormData("torrents", "test.torrent", requestBody);
        return request.add(part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
