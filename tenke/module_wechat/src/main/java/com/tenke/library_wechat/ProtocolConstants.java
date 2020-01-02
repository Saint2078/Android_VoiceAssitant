package com.tenke.library_wechat;

/**
 * Created by yhzhou on 2019/1/25 11:14
 */
public interface ProtocolConstants {


    String IN_EVENT_TYPE_KEY = "InEvent";

    String ACTION_VUI_COMMAND_IN = "com.telenav.voiceassistant.action.VOICE_ASSISTANT_IN";
    String ACTION_VUI_COMMAND_OUT = "com.telenav.voiceassistant.action.VOICE_ASSISTANT_OUT";
    String ACTION_VUI_WECHAT_OUT = "com.telenav.vui.action.WECHAT_OUT";
    String ACTION_VUI_TEXT_OUT = "com.telenav.vui.action.TEXT_OUT";

    //Data bundle keys
    String KEY_VOICE_STATE = "voice_state";
    String KEY_VOICE_AUDIO_LEVEL = "audio_level";
    String KEY_VOICE_VR_RESULT = "vr_result";
    String KEY_VOICE_INTENT = "voice_intent";
    String KEY_VOICE_RESPONSE_STR = "response_str";
    String KEY_VOICE_COMMAND_BUNDLE = "VoiceMessage";

    //Common
    String COMMON_QUIT = "COMMON_QUIT";
    //WeChat
    String WECHAT_LOGIN_STATUS = "WECHAT_LOGIN_STATUS";
    String WECHAT_FIND_CANDIDATES_DONE = "WECHAT_FIND_CANDIDATES_DONE";
    String WECHAT_RECEIVE_MESSAGE = "WECHAT_RECEIVE_MESSAGE";
    String WECHAT_REPLY_MSG_WITH_UI = "WECHAT_REPLY_MSG_WITH_UI";
    String WECHAT_MESSAGE_SEND_FAIL = "WECHAT_MESSAGE_SEND_FAIL";

    String WECHAT_DATA_KEY_IS_LOGIN = "isLogin";
    String WECHAT_DATA_KEY_FROM_INTENT = "fromIntent";
    String WECHAT_DATA_KEY_CANDIDATES_IDS = "candidatesIds";
    String WECHAT_DATA_KEY_CHAT_ID = "chatId";
    //Navigation
    String NAV_SEARCH_DONE = "searchDone";
    String NAV_ROUTE_PLAN_DONE = "routePlanDone";
    String NAV_WAYPOINT_ADDED = "waypointAdded";

    String NAV_DATA_KEY_SELECT_INDEX = "selected_index";
    String NAV_DATA_KEY_SEARCH_RESULT_NUM = "searchResultNum";
    String NAV_DATA_KEY_IS_IN_NAV = "isInNav";
    String NAV_DATA_KEY_IS_WAYPOINT_ADDED = "isWaypointAdded";
    //Music
    String MUSIC_SEARCH_NOT_FOUND = "musicNotFound";
    String MUSIC_BY_SONG_AND_ARTIST = "songAndArtist";
    String MUSIC_BY_CATEGORY = "category";
    String MUSIC_BY_ARTIST = "artist";


    String MUSIC_DATA_KEY_SONG_NAME = "musicSongName";
    String MUSIC_DATA_KEY_CATEGORY_NAME = "musicCategoryName";
    String MUSIC_DATA_KEY_ARTIST_NAME = "musicArtistName";
    //Phone
    String PHONE_IS_NOT_AVAILABLE = "phoneIsNotAvailable";
    String PHONE_BOOK_IS_NOT_AVAILABLE = "phoneBookIsNotAvailable";
    String PHONE_NAME_NOT_FOUND = "phoneNameNotFound";
    String PHONE_READY_TO_CALL = "phoneReadyToCall";
    String PHONE_CONFIRM_FUZZY_MATCH = "phoneConfirmFuzzyMatch";
    String PHONE_FIND_CANDIDATES_DONE = "PHONE_FIND_CANDIDATES_DONE";

    String PHONE_DATA_KEY_CONTACT_LIST_COUNT = "contactListCount";
    String PHONE_DATA_KEY_CONTACT_NAME = "contactName";
    String PHONE_DATA_KEY_CONTACT_NUMBER = "contactPhoneNumber";

    //WeChat
    String KEY_MESSAGE_TYPE = "msgType";
    String KEY_USERNAME = "userName";
    String KEY_GROUP_NICKNAME = "groupNickName";
    String KEY_USER_NICKNAME = "userNickName";
    String KEY_MESSAGE_CONTENT = "msgContent";
    String KEY_MESSAGE_HEAD_URL = "headUrl";
    String KEY_LAT = "lat";
    String KEY_LON = "lon";
    String KEY_POI_NAME = "poi";
    String KEY_ADDRESS = "addr";
    String KEY_VOICE_URL = "voUrl";
    String KEY_IMAGE_URL = "imUrl";
    String KEY_VIDEO_URL = "viUrl";
    String VALUE_TYPE_TEXT = "TEXT";
    String VALUE_TYPE_LOCATION = "LOCATION";
    String VALUE_TYPE_VOICE = "VOICE";
    String VALUE_TYPE_IMAGE = "IMAGE";
    String VALUE_TYPE_VIDEO = "VIDEO";
}
