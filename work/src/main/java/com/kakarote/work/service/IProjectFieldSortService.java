package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.common.project.ProjectFieldSortAddBO;
import com.kakarote.work.common.project.ProjectFieldSortQueryBO;
import com.kakarote.work.entity.PO.ProjectFieldSort;

import java.util.List;

/**
 * <p>
 * 字段排序表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
public interface IProjectFieldSortService extends BaseService<ProjectFieldSort> {

    /**
     * 功能描述: <br>
     * 〈查询字自定义字段段列表〉
     * @param  * @param projectFieldSortQueryBO
     * @return java.util.List<com.kakarote.work.entity.PO.ProjectFieldSort>
     * @author zyh
     */
    List<ProjectFieldSort> selectUserSort(ProjectFieldSortQueryBO projectFieldSortQueryBO);

    /**
     * 功能描述: <br>
     * 〈设置个人字段排序〉
     * @param  * @param projectFieldSortAddBO
     * @return java.lang.Boolean
     * @author zyh
     */
    Boolean addOrUpdateUserSort(ProjectFieldSortAddBO projectFieldSortAddBO);
}
