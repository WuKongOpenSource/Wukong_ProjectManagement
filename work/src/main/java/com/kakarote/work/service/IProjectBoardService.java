package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectBoard;

import java.util.List;

/**
 * <p>
 * 项目管理 看板信息 服务类
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
public interface IProjectBoardService extends BaseService<ProjectBoard> {
    /**
     * 功能描述: 新增看板
     * 〈〉
     * @Param:
     * @Return: 
     * @Author: guole
     * @Date: 2022/9/23 11:36
     */
    void  add(ProjectBoard projectBoard);
    /**
     * 功能描述: 修改看板顺序
     * 〈〉
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/23 11:36
     */
    void updateSorting(List<Integer> ids);

    void initialize();
}
