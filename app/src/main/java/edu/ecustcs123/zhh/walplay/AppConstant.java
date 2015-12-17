package edu.ecustcs123.zhh.walplay;/** * Created by BenZ on 15.12.8. * zhengbin0320@gmail.com */public class AppConstant{    class PlayerMsg {        public static final int PAUSE_MSG = 0;        public static final int PLAY_MSG = 1;        public static final int STOP_MSG = 2;        public static final int CONTINUE_MSG = 3;        public static final int NEXT_MSG = 4;        public static final int PREVIOUS_MSG = 5;        public static final int PROGRESS_CHANGE = 6;    }    class ACTION{        /*MUSIC*/        public static final String MUSIC_LISTPOS = "edu.ecustcs123.zhh.walplay.action.MUSIC_LISTPOS";        public static final String CTL_ACTION = "edu.ecustcs123.zhh.walplay.action.CTL_ACTION";        public static final String MUSIC_CURRENT = "edu.ecustcs123.zhh.walplay.action.MUSIC_CURRENT";        public static final String MUSIC_DURATION = "edu.ecustcs123.zhh.walplay.action.MUSIC_DURATION";        public static final String MODE_ACTION = "edu.ecustcs123.zhh.walplay.action.MODE_ACTION";        public static final String MUSIC_SERVICE = "edu.ecustcs123.zhh.walplay.PLAYER_SERVICE";        /*SPOT*/        public static final String GET_SPOT_REFRESH = "edu.ecustcs123.zhh.walplay.REFRESH_SPOT_GET";        public static final String SET_SPOT_REFRESH = "edu.ecustcs123.zhh.walplay.REFRESH_SPOT_SET";        public static final String SPOT_SERVICE = "edu.ecustcs123.zhh.walplay.SPOT_SERVICE";        public static final String MUSIC_CACHE = "edu.ecustcs123.zhh.walplay.action.MUSIC_CACHE";        /*LBS*/        public static final String START_LBS_SERVICE="edu.ecustcs123.zhh.walplay.action.START_LBS";        public static final String GET_LOC = "edu.ecustcs123.zhh.walplay.action.GET_LOC";        public static final String NOTIFY_SPOT = "edu.ecustcs123.zhh.walplay.action.NOTIFY_SPOT";    }    class PlayMode{        public static final int MODE_ROUND = 0;        public static final int MODE_REPEAT = 1;        public static final int MODE_RANDOM = 2;    }    class LOG{        public static final String LOG_PROGRESSBAR_CHANGED = "walP.pro_Changed";        public static final String LOG_GETPROGRESSCHANGE = "walP.get_Change";        public static final String LOG_CURRENTTIME = "walP.getCurrentTime";        public static final String LOG_DURATION = "walP.duration";        public static final String Fragment_oncreate = "WP_Frag_onCreate";        public static final String Fragment_onview = "WP_Frag_onCreateView";        public static final String Service_oncreate ="WP_Service_onCreate";        public static final String Service_onStart = "WP_Servece_onStartCmd";        public static final String Com_Frag_Aty = "WP_FragCom";        public static final String Test_PlayingInfo = "WPplayingINFO";        public static final String NewSpotPlaying = "WP_newSpot";        public static final String StartPlaying = "WP_start";        public static final String test151212 = "WP_Testing-----";        public static final String RemoteTest = "WP_RTEST------";        public static final String DownloadTest = "WP_DTEST------";        public static final String WPTEST = "WP_TEST_";        public static final String WPDEBUG = "WP_DEBUG_";        public static final String WPMAPDEBUG = "WP_MAP_DE_";        public static final String WPLBSDEBUG = "WP_LBS__";        public static final String WPSPOT = "WP_SPOT_";    }    class KEY{        public static final int CAMERA_RESULT = 1;        public static final String PARCELABLE_PLAYINGINFO = "walplay.key.playingInfo";        public static final String NEW_PLAYING = "walplay.key.newPlayingDetail";    }    class UPDATE_TYPE{        public static final int MUSIC_POS = 0;        public static final int MUSIC_CURRENT = 1;        public static final int MUSIC_DURATION = 2;    }}