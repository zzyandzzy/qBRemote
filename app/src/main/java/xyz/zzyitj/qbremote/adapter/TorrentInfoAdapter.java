package xyz.zzyitj.qbremote.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import xyz.zzyitj.qbremote.MainActivity;
import xyz.zzyitj.qbremote.MyApplication;
import xyz.zzyitj.qbremote.R;
import xyz.zzyitj.qbremote.api.TorrentsService;
import xyz.zzyitj.qbremote.enums.TorrentState;
import xyz.zzyitj.qbremote.model.TorrentInfo;
import xyz.zzyitj.qbremote.torrentlist.PlayPauseButton;
import xyz.zzyitj.qbremote.util.CompanyUtils;
import xyz.zzyitj.qbremote.util.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/5 12:55 下午
 * @email zzy.main@gmail.com
 */
public class TorrentInfoAdapter extends RecyclerView.Adapter<TorrentInfoAdapter.ViewHolder> {
    private static final String TAG = TorrentInfoAdapter.class.getSimpleName();

    private static final long ETA_INFINITE_THRESHOLD = TimeUnit.DAYS.toSeconds(7);

    private List<TorrentInfo> torrentInfoList;

    public Map<Integer, String> getSelectedItemsHashes() {
        return selectedItemsHashes;
    }

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> selectedItemsHashes = new HashMap<>();

    private MainActivity mainActivity;

    public TorrentInfoAdapter(List<TorrentInfo> torrentInfoList) {
        this.torrentInfoList = torrentInfoList;
    }

    @NonNull
    @Override
    public TorrentInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.torrent_list_item, parent, false);
        mainActivity = (MainActivity) MyApplication.instance.getContext();
        return new ViewHolder(view);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull TorrentInfoAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (mainActivity.actionMode != null) {
                v.setSelected(true);
                toggleSelection(position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mainActivity.actionMode != null) {
                return false;
            }
            mainActivity.actionMode = mainActivity.startActionMode(mainActivity.actionModeCallback);
            toggleSelection(position);
            return true;
        });
        TorrentInfo torrentInfo = torrentInfoList.get(position);
        boolean isCompleted = false;
        if (torrentInfo.getState().equals(TorrentState.uploading.getState())
                || torrentInfo.getState().equals(TorrentState.stalledUP.getState())
                || torrentInfo.getState().equals(TorrentState.queuedUP.getState())
                || torrentInfo.getState().equals(TorrentState.forcedUP.getState())) {
            isCompleted = true;
        }
        boolean isPaused = false;
        if (torrentInfo.getState().equals(TorrentState.pausedDL.getState())
                || torrentInfo.getState().equals(TorrentState.pausedUP.getState())) {
            isPaused = true;
        }
        boolean isRechecking = false;
        if (torrentInfo.getState().equals(TorrentState.checkingDL.getState())
                || torrentInfo.getState().equals(TorrentState.checkingResumeData.getState())
                || torrentInfo.getState().equals(TorrentState.checkingUP.getState())) {
            isRechecking = true;
        }
//        boolean isDownloading = false;
//        if (torrentInfo.getState().equals(TorrentState.downloading.getState())) {
//            isDownloading = true;
//        }
        holder.name.setText(torrentInfo.getName());
        holder.downloadSize.setText(CompanyUtils.getByteSum(torrentInfo.getDownloaded()));
        holder.downloadSpeed.setText(CompanyUtils.getByteSum(torrentInfo.getDlspeed()) + "/s");
        String radio = String.valueOf(torrentInfo.getRatio());
        if (radio.length() > 4) {
            radio = radio.substring(0, 4);
        }
        holder.uploadSize.setText(CompanyUtils.getByteSum(torrentInfo.getUploaded()) + "（分享率: " + radio + "）");
        holder.uploadSpeed.setText(CompanyUtils.getByteSum(torrentInfo.getUpspeed()) + "/s");
        holder.percentDone.setVisibility(!isCompleted || isRechecking ? View.VISIBLE : View.GONE);
        holder.remainingTime.setVisibility(isCompleted || isRechecking ? View.GONE : View.VISIBLE);
        if (isRechecking) {
            String progressText = MyApplication.instance.getContext().getString(R.string.checking_progress_text, torrentInfo.getProgress() * 100);
            holder.percentDone.setText(progressText);
        } else if (!isCompleted) {
            String progressText = String.format(Locale.getDefault(), "%.2f%%", 100 * torrentInfo.getProgress());
            holder.percentDone.setText(progressText);

            long eta = torrentInfo.getEta();
            String etaText;
            if (eta < 0) etaText = MyApplication.instance.getContext().getString(R.string.eta_unknown);
            else if (eta > ETA_INFINITE_THRESHOLD)
                etaText = MyApplication.instance.getContext().getString(R.string.eta_infinite);
            else
                etaText = MyApplication.instance.getContext().getString(R.string.eta, TextUtils.displayableTime(torrentInfo.getEta()));
            holder.remainingTime.setText(etaText);
        }

        holder.bar.setProgress((int) (torrentInfo.getProgress() * 100));
        int progressbarDrawable = R.drawable.torrent_progressbar;
        if (isPaused) {
            progressbarDrawable = R.drawable.torrent_progressbar_disabled;
        } else if (isRechecking) {
            progressbarDrawable = R.drawable.torrent_progressbar_rechecking;
        } else if (isCompleted) {
            progressbarDrawable = R.drawable.torrent_progressbar_finished;
        }
        holder.bar.setProgressDrawable(MyApplication.instance.getContext().getDrawable(progressbarDrawable));
        holder.state.setPaused(isPaused);
        int finalProgressbarDrawable = progressbarDrawable;
        holder.state.setOnClickListener(v -> {
            if (holder.state.isPaused()) {
                TorrentsService.resume(MyApplication.instance.getServer(), torrentInfo.getHash())
                        .subscribe(responseBody -> {
                            PlayPauseButton btn = (PlayPauseButton) v;
                            btn.toggle();
                            Log.i(TAG, "resume: " + responseBody.string());
                            if (finalProgressbarDrawable == R.drawable.torrent_progressbar_disabled) {
                                holder.bar.setProgressDrawable(MyApplication.instance.getContext().getDrawable(R.drawable.torrent_progressbar_finished));
                            } else {
                                holder.bar.setProgressDrawable(MyApplication.instance.getContext().getDrawable(finalProgressbarDrawable));
                            }
                        }, throwable -> {
                            Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                        });
            } else {
                TorrentsService.pause(MyApplication.instance.getServer(), torrentInfo.getHash())
                        .subscribe(responseBody -> {
                            PlayPauseButton btn = (PlayPauseButton) v;
                            btn.toggle();
                            Log.i(TAG, "pause: " + responseBody.string());
                            int progressbarDrawable1 = R.drawable.torrent_progressbar_disabled;
                            holder.state.setPaused(true);
                            holder.bar.setProgressDrawable(MyApplication.instance.getContext().getDrawable(progressbarDrawable1));
                        }, throwable -> {
                            Toast.makeText(MyApplication.instance.getContext(), "Error", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        if (torrentInfo.getState().equals(TorrentState.error.getState())) {
//            String errorMsg = torrentInfo.getErrorMessage();
//            if (errorMsg != null && !errorMsg.trim().isEmpty()) {
//                holder.errorMsgView.setVisibility(View.VISIBLE);
//                holder.errorMsgView.setText(errorMsg);
//                IconicsDrawable msgIcon = new IconicsDrawable(getContext(),
//                        error.isWarning() ? GoogleMaterial.Icon.gmd_warning : GoogleMaterial.Icon.gmd_error);
//                msgIcon.color(ColorUtils.resolveColor(context, android.R.attr.textColorSecondary, R.color.text_secondary));
//                int size = context.getResources().getDimensionPixelSize(R.dimen.torrent_list_error_icon_size);
//                msgIcon.setBounds(0, 0, size, size);
//                holder.errorMsgView.setCompoundDrawables(msgIcon, null, null, null);
//            } else {
//                holder.errorMsgView.setVisibility(View.GONE);
//            }
            holder.errorMsgView.setText("error");
        } else {
            holder.errorMsgView.setVisibility(View.GONE);
        }
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isSelected(int position) {
        return selectedItemsHashes.get(position) != null;
    }

    private void toggleSelection(int position) {
        String hash = torrentInfoList.get(position).getHash();
        if (selectedItemsHashes.get(position) != null) {
            selectedItemsHashes.remove(position);
        } else {
            selectedItemsHashes.put(position, hash);
        }
        notifyItemChanged(position);
        updateCABTitle();
        updateOptionsMenu();
    }

    private void updateCABTitle() {
        int count = getSelectedItemsCount();
        String text = mainActivity.getResources().getQuantityString(R.plurals.torrents, count, count);
        mainActivity.actionMode.setTitle(text);
    }

    private void updateOptionsMenu() {
        if (mainActivity.actionMode != null) {
            int count = getSelectedItemsCount();
            mainActivity.removeMenuItem.setEnabled(count > 0);
            mainActivity.pauseMenuItem.setEnabled(count > 0);
            mainActivity.startMenuItem.setEnabled(count > 0);
            mainActivity.renameMenuItem.setEnabled(count == 1);
            mainActivity.setLocationMenuItem.setEnabled(count > 0);
        }
    }

    public void selectAll() {
        for (int i = 0; i < getItemCount(); i++) {
            selectedItemsHashes.put(i, torrentInfoList.get(i).getHash());
            notifyItemChanged(i);
        }
        updateCABTitle();
        updateOptionsMenu();
    }

    public void clearSelection() {
        int[] positions = getSelectedItemsPositions();
        selectedItemsHashes.clear();
        for (int position : positions) {
            notifyItemChanged(position);
        }
        updateCABTitle();
        updateOptionsMenu();
    }

    public int[] getSelectedItemsPositions() {
        int[] positions = new int[getSelectedItemsCount()];
        int i = 0;
        for (Integer key : selectedItemsHashes.keySet()) {
            positions[i] = key;
            i++;
        }
        return positions;
    }

    @Override
    public int getItemCount() {
        return torrentInfoList.size();
    }

    public int getSelectedItemsCount() {
        return this.selectedItemsHashes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView downloadSize;
        TextView downloadSpeed;
        TextView uploadSize;
        TextView uploadSpeed;
        TextView percentDone;
        TextView remainingTime;
        PlayPauseButton state;
        ProgressBar bar;
        TextView errorMsgView;
        View selectedOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            downloadSize = itemView.findViewById(R.id.torrent_list_item_download);
            downloadSpeed = itemView.findViewById(R.id.torrent_list_item_download_speed);
            uploadSize = itemView.findViewById(R.id.torrent_list_item_upload);
            uploadSpeed = itemView.findViewById(R.id.torrent_list_item_upload_speed);
            percentDone = itemView.findViewById(R.id.torrent_list_item_percent_done);
            remainingTime = itemView.findViewById(R.id.torrent_list_item_remaining_time);
            state = itemView.findViewById(R.id.torrent_list_item_button);
            bar = itemView.findViewById(R.id.torrent_list_item_bar);
            errorMsgView = itemView.findViewById(R.id.torrent_list_item_error_message);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
        }
    }
}
