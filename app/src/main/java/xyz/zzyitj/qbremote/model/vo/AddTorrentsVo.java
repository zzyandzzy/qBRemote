package xyz.zzyitj.qbremote.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/10 8:12 下午
 * @email zzy.main@gmail.com
 * <p>
 * urls	string	URLs separated with newlines
 * torrents	raw	Raw data of torrent file. torrents can be presented multiple times.
 * savepath optional	string	Download folder
 * cookie optional	string	Cookie sent to download the .torrent file
 * category optional	string	Category for the torrent
 * skip_checking optional	string	Skip hash checking. Possible values are true, false (default)
 * paused optional	string	Add torrents in the paused state. Possible values are true, false (default)
 * root_folder optional	string	Create the root folder. Possible values are true, false, unset (default)
 * rename optional	string	Rename torrent
 * upLimit optional	integer	Set torrent upload speed limit. Unit in bytes/second
 * dlLimit optional	integer	Set torrent download speed limit. Unit in bytes/second
 * autoTMM optional	bool	Whether Automatic Torrent Management should be used
 * sequentialDownload optional	string	Enable sequential download. Possible values are true, false (default)
 * firstLastPiecePrio optional	string	Prioritize download first last piece. Possible values are true, false (default)
 */
@Getter
@Setter
@ToString
public class AddTorrentsVo {
    private String urls;
    private byte[] torrents;
    private String savepath;
    private String cookie;
    private String category;
    private Boolean skip_checking = false;
    private Boolean paused = false;
    private String root_folder = "unset";
    private String rename;
    private Integer upLimit;
    private Integer dlLimit;
    private Boolean autoTMM = false;
    private Boolean sequentialDownload = false;
    private Boolean firstLastPiecePrio = false;
}
