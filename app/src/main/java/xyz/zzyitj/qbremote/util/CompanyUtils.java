package xyz.zzyitj.qbremote.util;

import java.text.DecimalFormat;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/5 1:28 下午
 * @email zzy.main@gmail.com
 */
public class CompanyUtils {
    private static final Long BASE = 1024L;
    private static final String[] BASE_NAME = new String[]{
            "B", "KB", "MB", "GB", "TB", "PB", "ZB"
    };

    public static String getByteSum(Long length) {
        return getByteSum(length, 0);
    }

    private static String getByteSum(Long length, int depth) {
        if (length == null || length == 0 && depth == 0) {
            return 0 + " " + BASE_NAME[depth];
        }
        if (length < BASE) {
            DecimalFormat decimalFormat = new DecimalFormat(".00");
            return decimalFormat.format(length) + " " + BASE_NAME[depth];
        } else {
            length = length / BASE;
            return getByteSum(length, ++depth);
        }
    }
}
