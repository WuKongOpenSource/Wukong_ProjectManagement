package com.kakarote.work.common.project;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyq
 */
public class ProjectUtil {
    public static List<Long> toList(List<String> labelList) {
        List<Long> list = new ArrayList<>();
        if (labelList == null || labelList.size() == 0) {
            return list;
        }
        String regex=",";
        labelList.forEach(ids -> {
            if (StrUtil.isNotEmpty(ids)) {
                for (String id : ids.split(regex)) {
                    if (StrUtil.isNotEmpty(id)) {
                        list.add(Long.valueOf(id));
                    }
                }
            }
        });
        return list;
    }


    /**
    * 判断集合有无交集
    * @date 2020/11/17 11:09
    * @param commaSplice
    * @param ids
    * @return 
    **/
    public static boolean verifyIntersection(String commaSplice, List<Long> ids){
        if (CollUtil.isEmpty(ids)){
            return true;
        }
        List<Long> ownerUserIds = StrUtil.splitTrim(commaSplice, Const.SEPARATOR).stream().map(Long::valueOf).collect(Collectors.toList());;
        List<Long> intersection = ownerUserIds.stream().filter(ids::contains).collect(Collectors.toList());
        return intersection.size() > 0;
    }

    /**
     * 判断集合有无交集
     * @date 2020/11/17 11:09
     * @param module
     * @param source
     * @param target
     * @return
     **/
    public static String getLogContent(String module,Object source,Object target){
        String contentLog = ""
                .concat(" 将"+module);
        if(ObjectUtil.isEmpty(source)){
            contentLog = contentLog.concat("由 空" );
        }else{
            contentLog = contentLog.concat("从 " + source);
        }
        if(ObjectUtil.isEmpty(target)){
            contentLog = contentLog.concat(" 修改为 空");
        }else{
            contentLog = contentLog.concat(" 修改为 " + target);
        }

        return contentLog;
    }
}
