package xyz.zzyitj.qbremote.drawer;
;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import xyz.zzyitj.qbremote.enums.SortOrder;
import xyz.zzyitj.qbremote.enums.SortedBy;

public class SortDrawerItem extends PrimaryDrawerItem {

    private SortedBy sortedBy;
    private SortOrder sortOrder = null;

    public SortDrawerItem(SortedBy sortedBy) {
        this.sortedBy = sortedBy;
    }

    @Override
    public SortDrawerItem withName(@StringRes int nameRes) {
        super.withName(nameRes);
        return this;
    }

    public SortedBy getSortedBy() {
        return sortedBy;
    }

    public void setSortOrder(@Nullable SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        withBadge(sortOrder != null ? sortOrder.getSymbol() : "");
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
