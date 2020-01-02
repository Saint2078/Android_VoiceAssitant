package com.tenke.library_wechat;


public class RegexUtil {
    public static String WECHAT_AUTHENTICATION_GET_UUID = "(window.QRLogin.code = )([0-9]*); window.QRLogin.uuid = \"(\\S+?)\"";
    public static String WECHAT_AUTHENTICATION_WAIT_FOR_SCAN = "window.code=(\\d+);window.userAvatar = \'data:img/jpg;base64,(\\S+?)\';";
    public static String WECHAT_AUTHENTICATION_WAIT_FOR_LOGIN = "window.code=(\\d+);";
    public static String WECHAT_AUTHENTICATION_WAIT_FOR_LOGIN_REDIRECT_URL = "window.redirect_uri=\"(\\S+?)\";";
    public static String WECHAT_NEW_MESSAGE_LOOP = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
    public static String WECHAT_CHAT_SET_GROUP_USER_NAME = "@@.+?,";

    public static String WECHAT_GROUP_MESSAGE_PREFIX = "(@.+?):<br/>";

    //"@22a9b658e804b30590a5b006a9bf0c8f:<br/>你好"
    public static String WECHAT_GROUP_TEXT_MESSAGE_FROM_USER_NAME = "(@.+?):<br/>(.+?$)";

    //"上海市长宁区娄山关路523:<br/>/cgi-bin/mmwebwx-bin/webwxgetpubliclinkimg?url=xxx&msgid=1865506308771706744&pictype=location"
    public static String WECHAT_LOCATION_MESSAGE_FROM_USER_NAME = ".+?:";

    //"@9cfa37a670b2047957c859996ad2c879a64699765020d8f97ae8af23f2020988:<br/>上海市长宁区娄山关路523:<br/>/cgi-bin/mmwebwx-bin/webwxgetpubliclinkimg?url=xxx&msgid=3329207292774742228&pictype=location"
    public static String WECHAT_GROUP_LOCATION_MESSAGE_FROM_USER_NAME = "(@.+?):<br/>(.+?):";
}
