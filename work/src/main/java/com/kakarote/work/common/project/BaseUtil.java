package com.kakarote.work.common.project;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.util.Date;

/**
 * @author z
 * 一些通用方法
 */
public class BaseUtil {

    private static final Snowflake SNOWFLAKE;


    static {
        /*
            TODO 目前使用自动生成的工作节点ID和数据中心ID,可使用自定义的数据中心ID
         */

        SNOWFLAKE = IdUtil.getSnowflake();
    }

    /**
     * 获取当前年月的字符串
     *
     * @return yyyyMMdd
     */
    public static String getDate() {
        return DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
    }


    /**
     * 获取long类型的id，雪花算法
     * @return id
     */
    public static Long getNextId(){
        return SNOWFLAKE.nextId();
    }


    /**
     * 默认的上传文件路径
     */
    public final static String UPLOAD_PATH = BaseUtil.isWindows() ? "D:\\upload\\" : "/usr/local/upload/";

    /**
     * 获取当前是否是windows系统
     * @return true代表为真
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String getIp(){
        return "127.0.0.1";
    }
}
