package xyz.zzyitj.qbremote;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import xyz.zzyitj.qbremote.enums.InfoTorrentsVoFilter;
import xyz.zzyitj.qbremote.enums.SortOrder;
import xyz.zzyitj.qbremote.enums.SortedBy;
import xyz.zzyitj.qbremote.model.InfoTorrentFilter;
import xyz.zzyitj.qbremote.model.Server;
import xyz.zzyitj.qbremote.model.TorrentInfo;

import java.util.*;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 8:56 下午
 * @email zzy.main@gmail.com
 */
@Getter
@Setter
public class MyApplication extends Application {
    private static final String SHARED_PREFS_NAME = "qb_remote_shared_prefs";
    private static final String KEY_SERVER = "key_server";
    private static final String KEY_FILTER = "key_filter";
    private static final String KEY_SORTED_BY = "key_sorted_by";
    private static final String KEY_SORT_ORDER = "key_sort_order";

    public static MyApplication instance;
    private Context context;
    private InfoTorrentFilter filter;
    private Server server;
    private SortedBy sortedBy = SortedBy.NAME;
    private SortOrder sortOrder = SortOrder.ASCENDING;
    private int rid = 0;

    public interface OnSortingChangedListener {
        void onSortingChanged(Comparator<TorrentInfo> comparator);
    }

    private List<OnSortingChangedListener> sortingChangedListeners = new LinkedList<>();

    public void addOnSortingChangedListeners(@NonNull OnSortingChangedListener listener) {
        if (!sortingChangedListeners.contains(listener)) {
            sortingChangedListeners.add(listener);
        }
    }

    public void removeOnSortingChangedListener(@NonNull OnSortingChangedListener listener) {
        sortingChangedListeners.remove(listener);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public MyApplication(Context context) {
        instance = this;
        this.context = context;
        filter = new InfoTorrentFilter();
        filter.setName(this.context.getString(R.string.all));
        filter.setValue(InfoTorrentsVoFilter.ALL.getName());
        restore();
    }

    private void restore() {
        restoreSorting();
    }

    public void persist() {
        persistSorting();
    }

    private void persistSorting() {
        SharedPreferences sp = this.context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_SORTED_BY, sortedBy.ordinal());
        editor.putInt(KEY_SORT_ORDER, sortOrder.ordinal());
        editor.apply();
    }

    private void restoreSorting() {
        SharedPreferences sp = this.context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        sortedBy = SortedBy.values()[sp.getInt(KEY_SORTED_BY, 0)];
        sortOrder = SortOrder.values()[sp.getInt(KEY_SORT_ORDER, 0)];
    }

    public void setSorting(@NonNull SortedBy sortedBy, @NonNull SortOrder sortOrder) {
        this.sortedBy = sortedBy;
        this.sortOrder = sortOrder;

        Comparator<TorrentInfo> comparator = getSortComparator();
        for (OnSortingChangedListener listener : sortingChangedListeners) {
            listener.onSortingChanged(comparator);
        }
    }

    public Comparator<TorrentInfo> getSortComparator() {
        return sortOrder.comparator(sortedBy.getComparator());
    }

    public void saveServer(Server server) {
        SharedPreferences sp = this.context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String value = new Gson().toJson(server);
        editor.putString(KEY_SERVER, value);
        editor.apply();
    }

    public Server getServer() {
        SharedPreferences sp = this.context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String json = sp.getString(KEY_SERVER, null);
        return new Gson().fromJson(json, Server.class);
    }

    public void saveFilter(String name, String value) {
        filter.setName(name);
        filter.setValue(value);
        SharedPreferences sp = this.context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_FILTER, new Gson().toJson(filter));
        editor.apply();
    }

    public InfoTorrentFilter getFilters() {
        SharedPreferences sp = this.context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String json = sp.getString(KEY_FILTER, null);
        if (json != null) {
            filter = new Gson().fromJson(json, InfoTorrentFilter.class);
        }
        return filter;
    }
}
