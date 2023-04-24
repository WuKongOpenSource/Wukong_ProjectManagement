package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.work.entity.BO.ProjectGanttQueryBO;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.PO.ProjectTaskTime;
import com.kakarote.work.entity.VO.*;
import com.kakarote.work.mapper.ProjectTaskTimeMapper;
import com.kakarote.work.service.IProjectService;
import com.kakarote.work.service.IProjectTaskService;
import com.kakarote.work.service.IProjectTaskTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目事项工时表 服务实现类
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
@Service
public class ProjectTaskTimeServiceImpl extends BaseServiceImpl<ProjectTaskTimeMapper, ProjectTaskTime> implements IProjectTaskTimeService {

    @Autowired
    private IProjectTaskService projectTaskService;
    @Autowired
    private IProjectService projectService;

    /**
     * 获取项目事项工时列表
     *
     * @return
     */
    @Override
    public List<ProjectTaskTimeStatisticsVO> queryProjectTaskTimeList(ProjectGanttQueryBO projectGanttQueryBO) {
        List<ProjectTaskTimeStatisticsVO> statisticsVOS = new ArrayList<>();
        List<ProjectTask> belongIterationList = this.projectTaskService.lambdaQuery().eq(ProjectTask::getType, 1).
                eq(ProjectTask::getProjectId, projectGanttQueryBO.getProjectId()).and(StrUtil.isNotBlank(projectGanttQueryBO.getSearch()), wrapper -> wrapper.like(ProjectTask::getName, projectGanttQueryBO.getSearch()).or().eq(ProjectTask::getDescription, projectGanttQueryBO.getSearch()))
                .list();


        //获取迭代最早的开始时间
//       long minTime= belongIterationList.stream().mapToLong(i->i.getStartTime().getTime()).min().orElse(0);
//        //获取迭代最晚的结束时间
//       long maxTime= belongIterationList.stream().mapToLong(i->i.getStopTime().getTime()).max().orElse(0);
//        Instant minTimeInstant = Instant.ofEpochMilli(minTime);
//        Instant maxTimeInstant = Instant.ofEpochMilli(maxTime);
//
//        ZoneId zone = ZoneId.systemDefault();
        //  projectGanttQueryBO.setBeginTime(LocalDateTime.now());
        // projectGanttQueryBO.setEndTime(LocalDateTime.now().plusDays(30));

        List<ProjectTaskTimeListVO> taskTimeListVOS = baseMapper.queryProjectTaskTimeList(projectGanttQueryBO);
//        ProjectTaskTimeMaxAndMinVO taskTimeMaxAndMinVO = this.baseMapper.queryProjectTaskTimeMaxAndMin(projectGanttQueryBO.getProjectId());
//        if (ObjectUtil.isNotNull(taskTimeMaxAndMinVO)) {
//
//            for (ProjectTaskTimeListVO taskTimeListVO : taskTimeListVOS) {
//                List<ProjectTaskTime> taskTimes = this.lambdaQuery().eq(ProjectTaskTime::getTaskId, taskTimeListVO.getTaskId()).list();
//                taskTimeListVO.setTaskTimeVOS(taskTimes);
//                //  taskTimeListVO.setActualHourList(queryProjectTaskTime(taskTimeListVO.getTaskId(), projectGanttQueryBO));
//
//            }
//        }
        for (ProjectTaskTimeListVO taskTimeListVO : taskTimeListVOS) {
            List<ProjectTaskTime> taskTimes = this.lambdaQuery().eq(ProjectTaskTime::getTaskId, taskTimeListVO.getTaskId()).list();
            taskTimeListVO.setTaskTimeVOS(taskTimes);
            //  taskTimeListVO.setActualHourList(queryProjectTaskTime(taskTimeListVO.getTaskId(), projectGanttQueryBO));

        }
        for (ProjectTask projectTask : belongIterationList) {
            ProjectTaskTimeStatisticsVO taskTimeStatisticsVO = new ProjectTaskTimeStatisticsVO();
            taskTimeStatisticsVO.setTaskId(projectTask.getTaskId());
            taskTimeStatisticsVO.setName(projectTask.getName());
            taskTimeStatisticsVO.setTaskTimeListVOList(new ArrayList<>());
            for (ProjectTaskTimeListVO taskTimeListVO : taskTimeListVOS) {
                if (ObjectUtil.isNotNull(taskTimeListVO.getBelongIterationId()) && taskTimeListVO.getBelongIterationId().equals(projectTask.getTaskId())) {
                    taskTimeStatisticsVO.getTaskTimeListVOList().add(taskTimeListVO);

                }
            }
            statisticsVOS.add(taskTimeStatisticsVO);
        }
        return statisticsVOS;
    }

    @Override
    public List<ProjectGanttVO> getProjectGantt(ProjectGanttQueryBO projectGanttQueryBO) {
        //通用项目直接获取任务
        // 如果是敏捷项目 先获取迭代再获取迭代下的任务
        Project project = projectService.getProjectById(projectGanttQueryBO.getProjectId(),null);
        List<ProjectTask> taskList = this.projectTaskService.lambdaQuery().ne(ProjectTask::getType, 1).eq(ProjectTask::getProjectId, projectGanttQueryBO.getProjectId())
                .list();
        List<ProjectGanttVO> ganttVOS = new ArrayList<>();
        if (project.getType().equals(1)) {
            for (ProjectTask projectTask : taskList) {
                ProjectGanttVO ganttVO = new ProjectGanttVO();
                ganttVO.setTaskId(projectTask.getTaskId());
                ganttVO.setName(projectTask.getName());
                ganttVO.setStartTime(projectTask.getStartTime());
                ganttVO.setStopTime(projectTask.getStopTime());
                ganttVO.setChildren(new ArrayList<>());

                ganttVOS.add(ganttVO);
            }

        } else {
            List<ProjectTask> belongIterationList = this.projectTaskService.lambdaQuery().eq(ProjectTask::getType, 1).
                    eq(ProjectTask::getProjectId, projectGanttQueryBO.getProjectId()).and(StrUtil.isNotBlank(projectGanttQueryBO.getSearch()), wrapper -> wrapper.like(ProjectTask::getName, projectGanttQueryBO.getSearch()).or().eq(ProjectTask::getDescription, projectGanttQueryBO.getSearch()))
                    .list();

            for (ProjectTask projectTask : belongIterationList) {
                ProjectGanttVO ganttVO = new ProjectGanttVO();
                ganttVO.setTaskId(projectTask.getTaskId());
                ganttVO.setName(projectTask.getName());
                ganttVO.setStartTime(projectTask.getStartTime());
                ganttVO.setStopTime(projectTask.getStopTime());
                ganttVO.setChildren(new ArrayList<>());
                for (ProjectTask task : taskList) {
                    if (ObjectUtil.isNotNull(task.getBelongIterationId()) && task.getBelongIterationId().equals(ganttVO.getTaskId())) {
                        ProjectGanttTaskVO ganttTaskVO = BeanUtil.toBean(task, ProjectGanttTaskVO.class);
                        ganttVO.getChildren().add(ganttTaskVO);

                    }
                }
                ganttVOS.add(ganttVO);
            }

        }


        return ganttVOS;
    }

    /**
     * 根据项目事项ID获取工时
     *
     * @param taskId
     * @return
     */
    @Override
    public ProjectTaskTimeVO queryProjectTaskTimeByTaskId(Long taskId) {
        return baseMapper.queryProjectTaskTimeByTaskId(taskId);
    }

    /**
     * 获取工时
     *
     * @param taskId
     * @return
     */
    private List<Map<String, Object>> queryProjectTaskTime(Long taskId, ProjectGanttQueryBO projectGanttQueryBO) {
        //
        List<Map<String, Object>> list = baseMapper.queryProjectTaskTime(taskId, projectGanttQueryBO);
        return list;
    }


}
