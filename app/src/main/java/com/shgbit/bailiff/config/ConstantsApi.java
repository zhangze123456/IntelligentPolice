package com.shgbit.bailiff.config;

/**
 * @author:xushun on 2018/7/7
 * description :请求接口地址常量
 */

public class ConstantsApi {
    /**
     * 主域名 配置在assets目录下
     */
    public static String HOST;
    /**
     * 登录
     */
//    public static final String LOGIN_USER = "/icontrol/login";
    public static final String LOGIN_USER = "iexe/a/login";

    /**
     * 分层获取法院列表
     */
    public static String GET_COURTLIST = "/icontrol/court/getCourtList";

    /**
     * app更新
     */
    public static String APP_CHECK_UPDATE = "iexe/SystemContext/appVersionCheck.do";

}
