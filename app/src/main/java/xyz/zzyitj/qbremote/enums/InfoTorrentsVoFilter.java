package xyz.zzyitj.qbremote.enums;

import lombok.Getter;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/11 11:49 上午
 * @email zzy.main@gmail.com
 */
@Getter
public enum InfoTorrentsVoFilter {
    ALL("all"),
    DOWNLOADING("downloading"),
    COMPLETED("completed"),
    PAUSED("paused"),
    ACTIVE("active"),
    INACTIVE("inactive"),
    RESUMED("resumed");

    InfoTorrentsVoFilter(String name) {
        this.name = name;
    }

    private String name;
}
