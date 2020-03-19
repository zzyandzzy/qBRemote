package xyz.zzyitj.qbremote.util;

import xyz.zzyitj.qbremote.MyApplication;
import xyz.zzyitj.qbremote.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.YEAR;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TextUtils {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

    public static String displayableSize(long bytes) {
        double kbytes = bytes / 1024.0;
        if (kbytes < 1000)
            return String.format("%.1f KB", kbytes);

        double mbytes = bytes / (1024.0 * 1024.0);
        if (mbytes < 1000)
            return String.format("%.1f MB", mbytes);

        double gbytes = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.1f GB", gbytes);
    }

    public static String displayableTime(final long timeInSeconds) {
        long days = SECONDS.toDays(timeInSeconds);
        long hours = SECONDS.toHours(timeInSeconds) % 24;
        long minutes = SECONDS.toMinutes(timeInSeconds) % 60;
        long seconds = timeInSeconds % 60;

        StringBuilder b = new StringBuilder();
        if (days > 0) {
            b.append(days).append("d ");
            b.append(hours).append('h');
        } else if (hours > 0) {
            b.append(hours).append("h ");
            b.append(minutes).append('m');
        } else {
            if (minutes > 0) b.append(minutes).append("m ");
            b.append(seconds).append('s');
        }

        return b.toString();
    }

    public static String displayableDate(long timestampSeconds) {
        long timestampMillis = timestampSeconds * 1000;
        Date date = new Date(timestampMillis);
        String formattedTime = TIME_FORMAT.format(date);

        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeInMillis(timestampMillis);
        int year = timestamp.get(YEAR);
        int day = timestamp.get(DAY_OF_YEAR);
        Calendar today = Calendar.getInstance();
        if (year == today.get(YEAR) && day == today.get(DAY_OF_YEAR)) {
            return MyApplication.instance.getContext().getString(R.string.today_time, formattedTime);
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        if (year == yesterday.get(YEAR) && day == yesterday.get(DAY_OF_YEAR)) {
            return MyApplication.instance.getContext().getString(R.string.yesterday_time, formattedTime);
        }

        return MyApplication.instance.getContext().getString(R.string.date_time, DATE_FORMAT.format(date), formattedTime);
    }
}
