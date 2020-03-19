package xyz.zzyitj.qbremote.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/10 8:12 下午
 * @email zzy.main@gmail.com
 * <p>
 * <p>
 * Parameters:
 * <p>
 * filter optional	Filter torrent list. Allowed filters: all, downloading, completed, paused, active, inactive, resumed
 * category optional	Get torrents with the given category (empty string means "without category"; no "category" parameter means "any category")
 * sort optional	Sort torrents by given key. All the possible keys are listed here below
 * reverse optional	Enable reverse sorting. Possible values are true and false (default)
 * limit optional	Limit the number of torrents returned
 * offset optional	Set offset (if less than 0, offset from end)
 * hashes optional	Filter by hashes. Can contain multiple hashes separated by |
 * <p>
 * Example:
 * <p>
 * /api/v2/torrents/info?filter=downloading&category=sample%20category&sort=ratio
 * <p>
 * Returns:
 * <p>
 * HTTP Status Code	Scenario
 * 200	All scenarios- see JSON below
 */
@Getter
@Setter
@ToString
public class InfoTorrentsVo {
    private String filter = "all";
    private String category;
    private String sort;
    private Boolean reverse = false;
    private Integer limit;
    private Integer offset;
    private String[] hashes;
}
