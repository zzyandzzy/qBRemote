package xyz.zzyitj.qbremote.model;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/5 11:55 上午
 * @email zzy.main@gmail.com
 * <p>
 * Property	Type	Description
 * added_on	integer	Time (Unix Epoch) when the torrent was added to the client
 * amount_left	integer	Amount of data left to download (bytes)
 * auto_tmm	bool	Whether this torrent is managed by Automatic Torrent Management
 * category	string	Category of the torrent
 * completed	integer	Amount of transfer data completed (bytes)
 * completion_on	integer	Time (Unix Epoch) when the torrent completed
 * dl_limit	integer	Torrent download speed limit (bytes/s). -1 if ulimited.
 * dlspeed	integer	Torrent download speed (bytes/s)
 * downloaded	integer	Amount of data downloaded
 * downloaded_session	integer	Amount of data downloaded this session
 * eta	integer	Torrent ETA (seconds)
 * f_l_piece_prio	bool	True if first last piece are prioritized
 * force_start	bool	True if force start is enabled for this torrent
 * hash	string	Torrent hash
 * last_activity	integer	Last time (Unix Epoch) when a chunk was downloaded/uploaded
 * magnet_uri	string	Magnet URI corresponding to this torrent
 * max_ratio	float	Maximum share ratio until torrent is stopped from seeding/uploading
 * max_seeding_time	integer	Maximum seeding time (seconds) until torrent is stopped from seeding
 * name	string	Torrent name
 * num_complete	integer	Number of seeds in the swarm
 * num_incomplete	integer	Number of leechers in the swarm
 * num_leechs	integer	Number of leechers connected to
 * num_seeds	integer	Number of seeds connected to
 * priority	integer	Torrent priority. Returns -1 if queuing is disabled or torrent is in seed mode
 * progress	float	Torrent progress (percentage/100)
 * ratio	float	Torrent share ratio. Max ratio value: 9999.
 * ratio_limit	float	TODO (what is different from max_ratio?)
 * save_path	string	Path where this torrent's data is stored
 * seeding_time_limit	integer	TODO (what is different from max_seeding_time?)
 * seen_complete	integer	Time (Unix Epoch) when this torrent was last seen complete
 * seq_dl	bool	True if sequential download is enabled
 * size	integer	Total size (bytes) of files selected for download
 * state	string	Torrent state. See table here below for the possible values
 * super_seeding	bool	True if super seeding is enabled
 * tags	string	Comma-concatenated tag list of the torrent
 * time_active	integer	Total active time (seconds)
 * total_size	integer	Total size (bytes) of all file in this torrent (including unselected ones)
 * tracker	string	The first tracker with working status. (TODO: what is returned if no tracker is working?)
 * up_limit	integer	Torrent upload speed limit (bytes/s). -1 if ulimited.
 * uploaded	integer	Amount of data uploaded
 * uploaded_session	integer	Amount of data uploaded this session
 * upspeed	integer	Torrent upload speed (bytes/s)
 * <p>
 * sync/maindata
 * "amount_left": 3241807739,
 * "completed": 104448000,
 * "dlspeed": 123807,
 * "downloaded": 102431136,
 * "downloaded_session": 64435706,
 * "eta": 8640000,
 * "last_activity": 0,
 * "num_seeds": 0,
 * "progress": 0.031213394356766466,
 * "seen_complete": 1581413132,
 * "state": "pausedDL",
 * "time_active": 229
 */

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TorrentInfo {
    private Long added_on;
    private Long amount_left;
    private Boolean auto_tmm;
    private String category;
    private Long completed;
    private Long completion_on;
    private Integer dl_limit;
    private Long dlspeed;
    private Long downloaded;
    private Long downloaded_session;
    private Long eta;
    private Boolean f_l_piece_prio;
    private Boolean force_start;
    private String hash;
    private Long last_activity;
    private String magnet_uri;
    private Float max_ratio;
    private Long max_seeding_time;
    private String name;
    private Integer num_complete;
    private Integer num_incomplete;
    private Integer num_leechs;
    private Integer num_seeds;
    private Integer priority;
    private Float progress;
    private Float ratio;
    //    ratio_limit: -2;
    private String save_path;
    //    seeding_time_limit: -2;
    private Long seen_complete;
    private Boolean seq_dl;
    private Long size;
    private String state;
    private Boolean super_seeding;
    private String tags;
    private Long time_active;
    private Long total_size;
    private String tracker;
    //    up_limit: -1;
    private Long uploaded;
    //    uploaded_session: 0;
    private Long upspeed;
}
