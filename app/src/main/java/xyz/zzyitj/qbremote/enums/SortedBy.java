package xyz.zzyitj.qbremote.enums;


import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import xyz.zzyitj.qbremote.model.TorrentInfo;

import java.util.Comparator;

public enum SortedBy {

    NAME((t1, t2) -> {
        String name1 = Optional.fromNullable(t1.getName()).or("");
        String name2 = Optional.fromNullable(t2.getName()).or("");
        return name1.compareToIgnoreCase(name2);
    }),

    SIZE((t1, t2) -> {
        return Long.signum(t2.getSize() - t1.getSize());
    }),

    TIME_REMAINING((t1, t2) -> {
        return ComparisonChain.start()
                .compareFalseFirst(t1.getEta() < 0, t2.getEta() < 0)
                .compare(t1.getEta(), t2.getEta())
                .result();
    }),

    DATE_ADDED((t1, t2) -> {
        return Long.signum(t2.getAdded_on() - t1.getAdded_on());
    }),

    PROGRESS((t1, t2) -> {
        return Double.compare(t2.getProgress(), t1.getProgress());
    }),

    UPLOAD_RATIO((t1, t2) -> {
        return Double.compare(t1.getRatio(), t2.getRatio());
    });

    private Comparator<TorrentInfo> comparator;

    SortedBy(Comparator<TorrentInfo> comparator) {
        this.comparator = comparator;
    }

    public Comparator<TorrentInfo> getComparator() {
        return comparator;
    }
}
