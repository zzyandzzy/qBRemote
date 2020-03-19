package xyz.zzyitj.qbremote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/11 5:32 下午
 * @email zzy.main@gmail.com
 * <p>
 */
@Getter
@Setter
@ToString
public class SyncMainData {
    private Integer rid;
    private ServerState server_state;
    private Map<String, TorrentInfo> torrents;
}

@Getter
@Setter
@ToString
class ServerState {
    private Integer average_time_queue;
    private Long dl_info_data;
    private Long dl_info_speed;
    private Long total_buffers_size;
    private Long total_peer_connections;
    private Long total_wasted_session;
    private Long up_info_data;
}
