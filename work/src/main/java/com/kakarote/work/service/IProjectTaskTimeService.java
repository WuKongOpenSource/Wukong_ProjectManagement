package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.ProjectGanttQueryBO;
import com.kakarote.work.entity.PO.ProjectTaskTime;
import com.kakarote.work.entity.VO.ProjectGanttVO;
import com.kakarote.work.entity.VO.ProjectTaskTimeStatisticsVO;
import com.kakarote.work.entity.VO.ProjectTaskTimeVO;

import java.util.List;

/**
 * <p>
 * 项目事项工时表 服务类
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
public interface IProjectTaskTimeService extends BaseService<ProjectTaskTime> {

    /**
     * 获取项目事项工时列表
     *
     * @param projectGanttQueryBO
     * @return
     */
    List<ProjectTaskTimeStatisticsVO> queryProjectTaskTimeList(ProjectGanttQueryBO projectGanttQueryBO);

    /**
     * 获取项目事项工时列表
     *
     * @param projectTaskTimeQueryBO
     * @return
     */
    List<ProjectGanttVO> getProjectGantt(ProjectGanttQueryBO projectTaskTimeQueryBO);

    /**
     * 根据项目事项ID获取工时
     *
     * @param taskId
     * @return
     */
    ProjectTaskTimeVO queryProjectTaskTimeByTaskId(Long taskId);

}
