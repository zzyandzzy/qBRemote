package xyz.zzyitj.qbremote.util;

import xyz.zzyitj.qbremote.model.TorrentInfo;

import java.util.Collections;
import java.util.List;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/9 5:52 下午
 * @email zzy.main@gmail.com
 */
public class SortUtils {
    public static final int SORT_SIZE_ASC = 0;
    public static final int SORT_SIZE_DESC = 1;
    public static final int SORT_NAME = 2;
    public static final int SORT_ADD_ON_ASC = 3;
    public static final int SORT_ADD_ON_DESC = 4;
    public static final int SORT_PROGRESS_ASC = 5;
    public static final int SORT_PROGRESS_DESC = 6;
    public static final int SORT_RADIO_ASC = 7;
    public static final int SORT_RADIO_DESC = 8;

    /**
     * @param torrentInfoList TorrentInfoList
     * @param sortType        sort type
     */
    public static void quickSort(List<TorrentInfo> torrentInfoList, int sortType) {
        if (sortType == SORT_NAME) {
            Collections.sort(torrentInfoList, (o1, o2) -> o2.getName().compareTo(o1.getName()));
        } else {
            quickSort(torrentInfoList, 0, torrentInfoList.size() - 1, sortType);
        }
    }

    private static void quickSort(List<TorrentInfo> arr, int low, int high, int sortType) {
        int i, j;
        TorrentInfo temp, t;
        if (low > high) {
            return;
        }
        i = low;
        j = high;
        temp = arr.get(low);

        while (i < j) {
            if (sortType == SORT_SIZE_ASC) {
                while (temp.getSize() <= arr.get(j).getSize() && i < j) {
                    j--;
                }
                while (temp.getSize() >= arr.get(i).getSize() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_SIZE_DESC) {
                while (temp.getSize() >= arr.get(j).getSize() && i < j) {
                    j--;
                }
                while (temp.getSize() <= arr.get(i).getSize() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_ADD_ON_ASC) {
                while (temp.getAdded_on() <= arr.get(j).getAdded_on() && i < j) {
                    j--;
                }
                while (temp.getAdded_on() >= arr.get(i).getAdded_on() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_ADD_ON_DESC) {
                while (temp.getAdded_on() >= arr.get(j).getAdded_on() && i < j) {
                    j--;
                }
                while (temp.getAdded_on() <= arr.get(i).getAdded_on() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_PROGRESS_ASC) {
                while (temp.getProgress() <= arr.get(j).getProgress() && i < j) {
                    j--;
                }
                while (temp.getProgress() >= arr.get(i).getProgress() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_PROGRESS_DESC) {
                while (temp.getProgress() >= arr.get(j).getProgress() && i < j) {
                    j--;
                }
                while (temp.getProgress() <= arr.get(i).getProgress() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_RADIO_ASC) {
                while (temp.getRatio() <= arr.get(j).getRatio() && i < j) {
                    j--;
                }
                while (temp.getRatio() >= arr.get(i).getRatio() && i < j) {
                    i++;
                }
            } else if (sortType == SORT_RADIO_DESC) {
                while (temp.getRatio() >= arr.get(j).getRatio() && i < j) {
                    j--;
                }
                while (temp.getRatio() <= arr.get(i).getRatio() && i < j) {
                    i++;
                }
            }
            if (i < j) {
                t = arr.get(j);
                arr.set(j, arr.get(i));
                arr.set(i, t);
            }
        }
        arr.set(low, arr.get(i));
        arr.set(i, temp);
        quickSort(arr, low, j - 1, sortType);
        quickSort(arr, j + 1, high, sortType);
    }
}
