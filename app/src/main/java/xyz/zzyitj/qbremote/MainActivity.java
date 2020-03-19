package xyz.zzyitj.qbremote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import xyz.zzyitj.qbremote.activity.AddServerActivity;
import xyz.zzyitj.qbremote.activity.DonationActivity;
import xyz.zzyitj.qbremote.adapter.TorrentInfoAdapter;
import xyz.zzyitj.qbremote.api.AuthService;
import xyz.zzyitj.qbremote.api.SyncService;
import xyz.zzyitj.qbremote.api.TorrentsService;
import xyz.zzyitj.qbremote.drawer.SortDrawerItem;
import xyz.zzyitj.qbremote.enums.InfoTorrentsVoFilter;
import xyz.zzyitj.qbremote.enums.SortOrder;
import xyz.zzyitj.qbremote.enums.SortedBy;
import xyz.zzyitj.qbremote.model.InfoTorrentFilter;
import xyz.zzyitj.qbremote.model.TorrentInfo;
import xyz.zzyitj.qbremote.model.vo.AddTorrentsVo;
import xyz.zzyitj.qbremote.model.vo.InfoTorrentsVo;
import xyz.zzyitj.qbremote.util.IconUtils;
import xyz.zzyitj.qbremote.util.ThemeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DRAWER_ITEM_ID_SETTINGS = 1;
    private static final int DRAWER_ITEM_ID_DONATION = 2;
    private static final int REQUEST_CODE_CHOOSE_TORRENT = 102;
    private static final int REQUEST_READ_PERMISSIONS = 103;
    private static final String MIME_TYPE_TORRENT = "application/x-bittorrent";

    private boolean isSelect = false;

    private MyApplication myApplication;

    private Drawer drawer;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TorrentInfoAdapter torrentInfoAdapter;
    private List<TorrentInfo> torrentInfoList = new ArrayList<>();

    private MyApplication.OnSortingChangedListener sortingListener = comparator -> updateTorrentList();

    private Handler handler = new Handler();

    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, 3 * 1000);
            syncMainData();
        }
    };

    public ActionMode actionMode;
    public MenuItem removeMenuItem;
    public MenuItem pauseMenuItem;
    public MenuItem startMenuItem;
    public MenuItem renameMenuItem;
    public MenuItem setLocationMenuItem;
    public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_torrent_list_menu, menu);
            inflater.inflate(R.menu.torrent_actions_menu, menu);
            IconUtils.setMenuIcon(MyApplication.instance.getContext(), menu, R.id.action_select_all, GoogleMaterial.Icon.gmd_select_all);
            IconUtils.setMenuIcon(MyApplication.instance.getContext(), menu, R.id.action_remove_torrents, GoogleMaterial.Icon.gmd_delete);
            IconUtils.setMenuIcon(MyApplication.instance.getContext(), menu, R.id.action_pause, FontAwesome.Icon.faw_pause);
            IconUtils.setMenuIcon(MyApplication.instance.getContext(), menu, R.id.action_start, FontAwesome.Icon.faw_play);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            removeMenuItem = menu.findItem(R.id.action_remove_torrents);
            pauseMenuItem = menu.findItem(R.id.action_pause);
            startMenuItem = menu.findItem(R.id.action_start);
            renameMenuItem = menu.findItem(R.id.action_rename);
            setLocationMenuItem = menu.findItem(R.id.action_set_location);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            String[] hashes = new String[torrentInfoAdapter.getSelectedItemsCount()];
            int i = 0;
            for (Integer key : torrentInfoAdapter.getSelectedItemsHashes().keySet()) {
                hashes[i] = torrentInfoAdapter.getSelectedItemsHashes().get(key);
                i++;
            }
            switch (item.getItemId()) {
                case R.id.action_select_all:
                    if (torrentInfoAdapter.getSelectedItemsCount() < torrentInfoAdapter.getItemCount()) {
                        torrentInfoAdapter.selectAll();
                    } else {
                        torrentInfoAdapter.clearSelection();
                    }
                    return true;
                case R.id.action_remove_torrents:
                    showDeleteDialog(mode, hashes);
                    return true;
                case R.id.action_pause:
                    TorrentsService.pause(MyApplication.instance.getServer(), hashes)
                            .subscribe(responseBody -> {
                            }, throwable -> {
                                Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            });
                    mode.finish();
                    return true;
                case R.id.action_start:
                    TorrentsService.resume(MyApplication.instance.getServer(), hashes)
                            .subscribe(responseBody -> {
                            }, throwable -> {
                                Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            });
                    mode.finish();
                    return true;
                case R.id.action_rename:
                    if (torrentInfoAdapter.getSelectedItemsCount() == 1) {
                        String hash = hashes[0];
                        showRenameDialog(mode, hash);
                    }
                    return true;
                case R.id.action_set_location:
                    showChooseLocationDialog(mode, hashes);
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            torrentInfoAdapter.clearSelection();
            actionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MainActivity.this.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    };

    private void showRenameDialog(ActionMode mode, String hash) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.torrent_location_dialog, null);
        EditText locationEdit = rootView.findViewById(R.id.location_edit);
        Integer pos = torrentInfoAdapter.getSelectedItemsHashes().keySet().iterator().next();
        TorrentInfo torrentInfo = torrentInfoList.get(pos);
        locationEdit.setText(torrentInfo.getName());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.action_rename).setView(rootView)
                .setNegativeButton(Resources.getSystem().getIdentifier("cancel", "string", "android"), null)
                .setPositiveButton(Resources.getSystem().getIdentifier("ok", "string", "android"), (dialog1, which) -> {
                    if (TextUtils.isEmpty(locationEdit.getText())) {
                        Toast.makeText(MyApplication.instance.getContext(), "edit text not empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        TorrentsService.rename(MyApplication.instance.getServer(), locationEdit.getText().toString(), hash)
                                .subscribe(responseBody -> {
                                    Toast.makeText(MyApplication.instance.getContext(), "Ok", Toast.LENGTH_SHORT).show();
                                    mode.finish();
                                }, throwable -> {
                                    Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .create();
        dialog.show();
    }

    private void showChooseLocationDialog(ActionMode mode, String[] hashes) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.torrent_location_dialog, null);
        EditText locationEdit = rootView.findViewById(R.id.location_edit);
        Integer pos = torrentInfoAdapter.getSelectedItemsHashes().keySet().iterator().next();
        TorrentInfo torrentInfo = torrentInfoList.get(pos);
        locationEdit.setText(torrentInfo.getSave_path());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.action_set_location).setView(rootView)
                .setNegativeButton(Resources.getSystem().getIdentifier("cancel", "string", "android"), null)
                .setPositiveButton(Resources.getSystem().getIdentifier("ok", "string", "android"), (dialog1, which) -> {
                    if (TextUtils.isEmpty(locationEdit.getText())) {
                        Toast.makeText(MyApplication.instance.getContext(), "edit text not empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        TorrentsService.location(MyApplication.instance.getServer(), locationEdit.getText().toString(), hashes)
                                .subscribe(responseBody -> {
                                    Toast.makeText(MyApplication.instance.getContext(), "Ok", Toast.LENGTH_SHORT).show();
                                    mode.finish();
                                }, throwable -> {
                                    Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .create();
        dialog.show();
    }

    private void showDeleteDialog(ActionMode mode, String[] hashes) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.torrent_delete_dialog, null);
        CheckBox deleteFilesCheckbox = rootView.findViewById(R.id.delete_files_checkbox);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.action_remove_torrents).setView(rootView)
                .setNegativeButton(Resources.getSystem().getIdentifier("cancel", "string", "android"), null)
                .setPositiveButton(Resources.getSystem().getIdentifier("ok", "string", "android"),
                        (dialog1, which) -> TorrentsService.delete(MyApplication.instance.getServer(), deleteFilesCheckbox.isChecked(), hashes)
                                .subscribe(responseBody -> {
                                    for (int i = 0; i < torrentInfoList.size(); i++) {
                                        for (String s : hashes) {
                                            if (torrentInfoList.get(i).getHash().equals(s)) {
                                                torrentInfoList.remove(i);
                                                torrentInfoAdapter.notifyItemRemoved(i);
                                            }
                                        }
                                    }
                                    Toast.makeText(MyApplication.instance.getContext(), "Ok", Toast.LENGTH_SHORT).show();
                                    mode.finish();
                                }, throwable -> {
                                    Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }))
                .create();
        dialog.show();
    }

    private void updateTorrentList() {
        Comparator<TorrentInfo> comparator = myApplication.getSortComparator();
        if (comparator != null) {
            Collections.sort(torrentInfoList, comparator);
        }
        torrentInfoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (myApplication.getServer() == null) {
            Intent intent = new Intent(this, AddServerActivity.class);
            startActivity(intent);
        } else {
            initViews();
            initDatas();
            handler.postDelayed(task, 3000);
        }
    }

    @SuppressLint("CheckResult")
    private void syncMainData() {
        SyncService.mainData(myApplication.getServer(), myApplication.getRid())
                .subscribe(syncMainData -> {
//                    Log.i(TAG, "syncMainData: " + myApplication.getRid());
                    if (myApplication.getRid() != 0) {
                        for (String key : syncMainData.getTorrents().keySet()) {
                            for (int i = 0; i < torrentInfoList.size(); i++) {
                                if (key.equals(torrentInfoList.get(i).getHash())) {
                                    TorrentInfo modifyTorrentInfo = syncMainData.getTorrents().get(key);
                                    TorrentInfo torrentInfo = torrentInfoList.get(i);
                                    // state
                                    if (modifyTorrentInfo.getState() != null) {
                                        torrentInfo.setState(modifyTorrentInfo.getState());
                                    }
                                    // download speed
                                    if (modifyTorrentInfo.getDlspeed() != null) {
                                        torrentInfo.setDlspeed(modifyTorrentInfo.getDlspeed());
                                    }
                                    // download size
                                    if (modifyTorrentInfo.getDownloaded() != null) {
                                        torrentInfo.setDownloaded(modifyTorrentInfo.getDownloaded());
                                    }
                                    // upload speed
                                    if (modifyTorrentInfo.getUpspeed() != null) {
                                        torrentInfo.setUpspeed(modifyTorrentInfo.getUpspeed());
                                    }
                                    // upload size
                                    if (modifyTorrentInfo.getDownloaded() != null) {
                                        torrentInfo.setUploaded(modifyTorrentInfo.getUploaded());
                                    }
                                    // progress
                                    if (modifyTorrentInfo.getProgress() != null) {
                                        torrentInfo.setProgress(modifyTorrentInfo.getProgress());
                                    }
                                    // eta
                                    if (modifyTorrentInfo.getEta() != null) {
                                        torrentInfo.setEta(modifyTorrentInfo.getEta());
                                    }
                                    // radio
                                    if (modifyTorrentInfo.getRatio() != null) {
//                                        Log.i(TAG, "syncMainData: radio: " + modifyTorrentInfo.getRatio());
                                        torrentInfo.setRatio(modifyTorrentInfo.getRatio());
                                    }
                                    // name
                                    if (modifyTorrentInfo.getName() != null) {
                                        torrentInfo.setName(modifyTorrentInfo.getName());
                                    }
                                    torrentInfoList.set(i, torrentInfo);
                                    torrentInfoAdapter.notifyItemChanged(i);
                                }
                            }
                        }
                    }
                    myApplication.setRid(syncMainData.getRid());
                }, throwable -> {
                    Log.e(TAG, "syncMainData: ", throwable);
                });
    }

    @SuppressLint("CheckResult")
    private void initDatas() {
        torrentInfoAdapter = new TorrentInfoAdapter(torrentInfoList);
        recyclerView.setAdapter(torrentInfoAdapter);
        recyclerView.getItemAnimator().setChangeDuration(0);
        AuthService.login(myApplication.getServer())
                .subscribe(responseBody -> {
                    Log.v(TAG, responseBody.string());
                    InfoTorrentsVo infoTorrentsVo = new InfoTorrentsVo();
                    infoTorrentsVo.setFilter(myApplication.getFilter().getValue());
                    TorrentsService.info(myApplication.getServer(), infoTorrentsVo)
                            .subscribe(this::initAdapter, throwable -> {
                                Log.e(TAG, "Connection error.", throwable);
                                Toast.makeText(this, "Connection error.", Toast.LENGTH_SHORT).show();
                            });
                }, throwable -> {
                    Log.e(TAG, "Connection error.", throwable);
                    Toast.makeText(this, "Connection error.", Toast.LENGTH_SHORT).show();
                });
        fab.setOnClickListener(view -> {
            checkPermissionAndSelectTorrent();
        });
    }

    private void checkPermissionAndSelectTorrent() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showFileChooser();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSIONS);
        }
    }

    private void initAdapter(List<TorrentInfo> torrentInfoList) {
        progressBar.setVisibility(View.GONE);
        this.torrentInfoList = torrentInfoList;
        Log.i(TAG, "initAdapter: " + torrentInfoList.size());
//        for (TorrentInfo torrentInfo : torrentInfoList) {
//            Log.i(TAG, "initAdapter: " + torrentInfo.getSave_path());
//        }
        torrentInfoAdapter = new TorrentInfoAdapter(this.torrentInfoList);
        recyclerView.setAdapter(torrentInfoAdapter);
        updateTorrentList();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.filter_spinner, R.layout.toolbar_spinner_item);
        Spinner filterSpinner = new Spinner(getSupportActionBar().getThemedContext());
        filterSpinner.setAdapter(spinnerAdapter);
        String[] filterArr = getResources().getStringArray(R.array.filter_spinner);
        InfoTorrentFilter filter = myApplication.getFilters();
        for (int i = 0; i < filterArr.length; i++) {
            if (filterArr[i].equals(filter.getName())) {
                filterSpinner.setSelection(i);
            }
        }
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterName = (String) parent.getItemAtPosition(position);
                String filter = InfoTorrentsVoFilter.ALL.getName();
                if (position == 1) {
                    filter = InfoTorrentsVoFilter.DOWNLOADING.getName();
                } else if (position == 2) {
                    filter = InfoTorrentsVoFilter.COMPLETED.getName();
                } else if (position == 3) {
                    filter = InfoTorrentsVoFilter.PAUSED.getName();
                } else if (position == 4) {
                    filter = InfoTorrentsVoFilter.ACTIVE.getName();
                } else if (position == 5) {
                    filter = InfoTorrentsVoFilter.INACTIVE.getName();
                } else if (position == 6) {
                    filter = InfoTorrentsVoFilter.RESUMED.getName();
                }
                myApplication.saveFilter(filterName, filter);
                InfoTorrentsVo infoTorrentsVo = new InfoTorrentsVo();
                infoTorrentsVo.setFilter(filter);
                if (isSelect) {
                    TorrentsService.info(myApplication.getServer(), infoTorrentsVo)
                            .subscribe(torrentInfoList -> MainActivity.this.initAdapter(torrentInfoList),
                                    throwable -> {
                                        Log.e(TAG, "Connection error.", throwable);
                                        Toast.makeText(MainActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
                                    });
                }
                isSelect = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toolbar.addView(filterSpinner, 0);
        fab = findViewById(R.id.fab);

        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withName(R.string.action_settings)
                .withIdentifier(DRAWER_ITEM_ID_SETTINGS)
                .withIcon(R.drawable.ic_settings)
                .withSelectable(false);

        PrimaryDrawerItem donationItem = new PrimaryDrawerItem().withName(R.string.donation)
                .withIdentifier(DRAWER_ITEM_ID_DONATION)
                .withIcon(R.drawable.ic_thumb_up)
                .withSelectable(false);

        SwitchDrawerItem nightModeItem = new SwitchDrawerItem().withName(R.string.night_mode)
                .withIcon(R.drawable.ic_brightness)
                .withSelectable(false)
                .withChecked(ThemeUtils.isInNightMode(this))
                .withOnCheckedChangeListener((drawerItem, buttonView, isChecked) -> switchTheme(isChecked));

        final SortDrawerItem[] sortItems = new SortDrawerItem[]{
                new SortDrawerItem(SortedBy.NAME).withName(R.string.drawer_sort_by_name),
                new SortDrawerItem(SortedBy.DATE_ADDED).withName(R.string.drawer_sort_by_date_added),
                new SortDrawerItem(SortedBy.SIZE).withName(R.string.drawer_sort_by_size),
                new SortDrawerItem(SortedBy.TIME_REMAINING).withName(R.string.drawer_sort_by_time_remaining),
                new SortDrawerItem(SortedBy.PROGRESS).withName(R.string.drawer_sort_by_progress),
                new SortDrawerItem(SortedBy.UPLOAD_RATIO).withName(R.string.drawer_sort_by_upload_ratio)
        };

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(new SectionDrawerItem().withName(R.string.drawer_sort_by).withDivider(false))
                .addDrawerItems(sortItems)
                .addStickyDrawerItems(nightModeItem, donationItem, settingsItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SortDrawerItem) {
                            handleSortItemClick((SortDrawerItem) drawerItem);
                            return true;
                        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_ID_SETTINGS) {
                            Toast.makeText(MainActivity.this, "暂未开发!", Toast.LENGTH_SHORT).show();
                        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_ID_DONATION) {
                            startActivity(new Intent(MainActivity.this, DonationActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "pos: " + position, Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }

                    private void handleSortItemClick(SortDrawerItem selectedItem) {
                        SortOrder prevSortOrder = selectedItem.getSortOrder();
                        SortOrder sortOrder;
                        if (prevSortOrder == null) sortOrder = SortOrder.ASCENDING;
                        else
                            sortOrder = prevSortOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING;
                        for (SortDrawerItem item : sortItems) {
                            if (item != selectedItem) {
                                item.setSortOrder(null);
                                item.withSetSelected(false);
                                drawer.updateItem(item);
                            }
                        }
                        selectedItem.setSortOrder(sortOrder);
                        drawer.updateItem(selectedItem);
                        myApplication.setSorting(selectedItem.getSortedBy(), sortOrder);
                    }
                })
                .build();

        SortedBy persistedSortedBy = myApplication.getSortedBy();
        SortOrder persistedSortOrder = myApplication.getSortOrder();
        for (SortDrawerItem item : sortItems) {
            if (item.getSortedBy() == persistedSortedBy) {
                item.setSortOrder(persistedSortOrder);
                item.withSetSelected(true);
                break;
            }
        }
        progressBar = findViewById(R.id.progress);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void init() {
        myApplication = new MyApplication(this);
        myApplication.addOnSortingChangedListeners(sortingListener);
        if (ThemeUtils.isInNightMode(this)) {
            ThemeUtils.setIsInNightMode(this, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_open_torrent:
                checkPermissionAndSelectTorrent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchTheme(boolean nightMode) {
        ThemeUtils.setIsInNightMode(this, nightMode);
        recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myApplication.getServer() == null) {
            Intent intent = new Intent(this, AddServerActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApplication.persist();
    }

    @Override
    protected void onDestroy() {
        myApplication.removeOnSortingChangedListener(sortingListener);
        super.onDestroy();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME_TYPE_TORRENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_torrent_file)),
                    MainActivity.REQUEST_CODE_CHOOSE_TORRENT);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_install_file_manager_msg),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    Toast.makeText(MainActivity.this, "Please get this app read permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_TORRENT) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    getFileInputStream(uri);
                }
            }
        }
    }

    private void getFileInputStream(Uri uri) {
        try {
            openTorrentByLocalFile(getContentResolver().openInputStream(uri));
        } catch (IOException ex) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                String msg = getString(R.string.error_file_does_not_exists_msg, uri.toString());
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return;
            }
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String fileName = cursor.getString(nameIndex);
            cursor.close();
            String extension = FilenameUtils.getExtension(fileName);
            if (!"torrent".equals(extension)) {
                String msg = getResources().getString(R.string.error_wrong_file_extension_msg, extension);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return;
            }
            try {
                openTorrentByLocalFile(getContentResolver().openInputStream(Uri.parse(fileName)));
            } catch (FileNotFoundException e) {
                String msg = getResources().getString(R.string.error_file_does_not_exists_msg, fileName);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void openTorrentByLocalFile(InputStream inputStream) {
        try {
            AddTorrentsVo addTorrentsVo = new AddTorrentsVo();
            addTorrentsVo.setTorrents(IOUtils.toByteArray(inputStream));
            TorrentsService.add(myApplication.getServer(), addTorrentsVo)
                    .subscribe(responseBody -> {
                        String body = responseBody.string();
                        Log.i(TAG, "onActivityResult: " + body);
                        if (body.contains("Fails")) {
                            Toast.makeText(MainActivity.this, R.string.torrent_added_fail, Toast.LENGTH_SHORT).show();
                        } else if (body.contains("Ok")) {
                            Toast.makeText(MainActivity.this, R.string.torrent_added_successfully, Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        Log.e(TAG, "onActivityResult: ", throwable);
                        Toast.makeText(MainActivity.this, R.string.torrent_added_fail, Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            Log.e(TAG, "Failed to read file stream", e);
            Toast.makeText(MainActivity.this, getString(R.string.error_cannot_read_file_msg), Toast.LENGTH_SHORT).show();
            return;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close input stream", e);
            }
        }
    }
}
