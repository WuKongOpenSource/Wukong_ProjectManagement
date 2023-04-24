package com.kakarote.work.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.entity.VO.ProjectTaskLogVO;
import com.kakarote.work.mapper.ProjectTaskLogMapper;
import com.kakarote.work.service.IProjectTaskLogService;
import com.kakarote.work.service.IProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务日志表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-14
 */
@Service
public class ProjectTaskLogServiceImpl extends BaseServiceImpl<ProjectTaskLogMapper, ProjectTaskLog> implements IProjectTaskLogService {


    @Autowired
    IProjectTaskService projectTaskService;


    @Override
    public List<ProjectTaskLogVO> queryTaskLog(Long taskId, Long type) {
        List<Long> resultTaskId = new ArrayList<>();
        resultTaskId.add(taskId);
        queryChildTaskId(resultTaskId, taskId);
        List<ProjectTaskLogVO> projectTaskLogVos = new ArrayList<>();
        List<ProjectTaskLog> projectTaskLogs = lambdaQuery().in(ProjectTaskLog::getTaskId, resultTaskId).eq(ProjectTaskLog::getType, type).list();
        if (CollectionUtil.isNotEmpty(projectTaskLogs)) {
            projectTaskLogs.stream().forEach(p -> {
                ProjectTaskLogVO projectTaskLogVO = new ProjectTaskLogVO();
                projectTaskLogVO.setContent(p.getContent());
                projectTaskLogVO.setCreateTime(p.getCreateTime());
                projectTaskLogVO.setLogId(p.getLogId());
                UserInfo userInfo = UserCacheUtil.getUserInfo(p.getUserId());
                projectTaskLogVO.setImg(userInfo.getUserImg());
                projectTaskLogVO.setRealname(userInfo.getNickname());
                projectTaskLogVos.add(projectTaskLogVO);
            });
        }
        return projectTaskLogVos.stream().sorted(Comparator.comparing(ProjectTaskLogVO::getCreateTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed()).collect(Collectors.toList());
    }

    @Override
    public void saveTaskLog(ProjectTaskLog projectTaskLog) {
        projectTaskLog.setCreateUserId(UserUtil.getUserId());
        projectTaskLog.setUserId(UserUtil.getUserId());
        save(projectTaskLog);
    }

    private void queryChildTaskId(List<Long> resultTaskId, Long taskId) {
        List<ProjectTask> belongIterationId = projectTaskService.lambdaQuery().in(ProjectTask::getBelongIterationId, taskId).in(ProjectTask::getType, Arrays.asList(2, 3, 4)).list().stream().collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(belongIterationId)) {
            resultTaskId.addAll(belongIterationId.stream().map(p -> p.getTaskId()).collect(Collectors.toList()));
            List<Long> pIds = belongIterationId.stream().map(p -> p.getPid()).collect(Collectors.toList());
            resultTaskId.addAll(pIds);
        }
        List<ProjectTask> belatedDemandId = projectTaskService.lambdaQuery().in(ProjectTask::getRelatedDemandId, taskId).in(ProjectTask::getType, Arrays.asList(2, 3, 4)).list().stream().collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(belatedDemandId)) {
            resultTaskId.addAll(belatedDemandId.stream().map(p -> p.getTaskId()).collect(Collectors.toList()));
            List<Long> pIds = belatedDemandId.stream().map(p -> p.getPid()).collect(Collectors.toList());
            resultTaskId.addAll(pIds);
        }
    }

    @Override
    public void saveTaskLog(Long taskId, String content) {
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(taskId);
        projectTaskLog.setType(1);
        projectTaskLog.setContent(content);
        this.saveTaskLog(projectTaskLog);
    }
}
