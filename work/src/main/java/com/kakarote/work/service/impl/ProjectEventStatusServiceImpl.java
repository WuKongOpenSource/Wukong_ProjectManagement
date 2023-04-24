package com.kakarote.work.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.constant.ProjectCodeEnum;
import com.kakarote.work.entity.BO.ProjectTransferStatusBO;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectBoardStatus;
import com.kakarote.work.entity.PO.ProjectEventStatus;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.mapper.ProjectEventStatusMapper;
import com.kakarote.work.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 事件属性表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@Service
public class ProjectEventStatusServiceImpl extends BaseServiceImpl<ProjectEventStatusMapper, ProjectEventStatus> implements IProjectEventStatusService {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectTaskService projectTaskService;
    @Autowired
    private IProjectBoardStatusService boardStatusService;
    @Autowired
    private IProjectEventStatusService eventStatusService;
    @Autowired
    private IProjectSchemeRelationBoardService schemeRelationBoardService;

    @Override
    public void add(ProjectEventStatus projectEventStatus) {
        if (this.lambdaQuery().eq(ProjectEventStatus::getProjectId, projectEventStatus.getProjectId()).
                eq(ProjectEventStatus::getProjectEventId, projectEventStatus.getProjectEventId()).
                eq(ProjectEventStatus::getProjectStatusId, projectEventStatus.getProjectStatusId()).count() > 0) {

            throw new BusinessException(ProjectCodeEnum.PROJECT_EVENT_STATUS_REPETITION_ERROR);

        }
        projectEventStatus.setInitStatus(0);
        projectEventStatus.setCreateUserId(UserUtil.getUserId());
        projectEventStatus.setProjectId(projectEventStatus.getProjectId());
        projectEventStatus.setSorting(this.lambdaQuery().
                eq(ProjectEventStatus::getProjectEventId, projectEventStatus.getProjectEventId()).count());
        this.baseMapper.insert(projectEventStatus);
    }

    @Override
    public void update(ProjectEventStatus projectEventStatus) {
        projectEventStatus.setUpdateUserId(UserUtil.getUserId());
        this.baseMapper.updateById(projectEventStatus);
    }

    @Override
    public List<ProjectEventStatus> queryEventStatusByEventId(Long eventId) {
        return this.baseMapper.queryEventStatusByEventId(eventId);
    }

    @Override
    public List<ProjectEventStatus> queryEventStatusByEventId(Long eventId, Integer sysType,Long projectId) {

        return this.baseMapper.queryEventStatusByEventIdAndSysType(eventId, sysType,projectId);
    }

    @Override
    public List<ProjectEventStatus> queryEventStatusByEventIdAndUseStatus(Long eventId, Integer userStatus) {
        return this.baseMapper.queryEventStatusByEventIdAndUseStatus(eventId, userStatus);
    }

    @Override
    public List<ProjectEventStatus> notAddStatus(Long eventId, Long schemeRelationId,Long projectId) {
        return this.baseMapper.notAddStatus(eventId, schemeRelationId,projectId);
    }

    @Override
    public void updateSorting(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            this.lambdaUpdate().eq(ProjectEventStatus::getId, ids.get(i)).set(ProjectEventStatus::getSorting, i + 1).update();
        }
    }

    @Override
    public List<ProjectEventStatus> notUserStatusList(Long eventId) {
        Long noUserStatusCount = this.lambdaQuery().eq(ProjectEventStatus::getProjectEventId, eventId).eq(ProjectEventStatus::getUseStatus, 0).count();
        if (noUserStatusCount == 0L) {
            List<ProjectEventStatus> projectEventStatuses = this.lambdaQuery().eq(ProjectEventStatus::getProjectEventId, eventId).eq(ProjectEventStatus::getUseStatus, 0).list();
            projectEventStatuses.forEach(projectEventStatus -> {
                projectEventStatus.setId(null);
                projectEventStatus.setUseStatus(0);
            });
        }
        this.queryEventStatusByEventIdAndUseStatus(eventId, 0);
        return null;
    }

    @Override
    public void delete(Long id) {
        this.lambdaUpdate().eq(ProjectEventStatus::getId, id).set(ProjectEventStatus::getUseStatus, 2).update();
        //  removeById(id);
    }

    @Override
    public ProjectEventStatus queryEventStatusById(Long id) {
        return this.baseMapper.queryEventStatusById(id);
    }

    @Override
    public List<ProjectEventStatus> applicationSchemeStatusList(Long projectId, Long eventId) {
        if (ObjectUtil.isEmpty(projectId)) {
            return new ArrayList<>();
        }
        Project project = projectService.getById(projectId);
        if (ObjectUtil.isEmpty(project)) {
            return new ArrayList<>();
        }
        //根据项目id查询状态信息
        List<ProjectEventStatus> projectEventStatuses = this.baseMapper.applicationSchemeProjectStatusList(projectId, eventId);
        if (CollectionUtil.isEmpty(projectEventStatuses)) {
            projectService.initEventStatus(projectId, eventId);
            projectEventStatuses = this.baseMapper.applicationSchemeProjectStatusList(projectId, eventId);
        }

        return projectEventStatuses;

    }

    @Override
    public boolean saveOrUpdateBatch(Collection<ProjectEventStatus> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferStatus(ProjectTransferStatusBO projectTransferStatusBO) {
        // 迁移状态
        //删除 未启用删除的状态
        //设置 新增未启用状态
        for (int i = 0; i < projectTransferStatusBO.getSourceStatus().size(); i++) {
            Integer statusType = eventStatusService.queryEventStatusById(projectTransferStatusBO.getTransferStatus().get(i)).getStatusType();

            projectTaskService.lambdaUpdate().eq(ProjectTask::getBoardStatusId, projectTransferStatusBO.getSourceStatus().get(i)).
                    set(ProjectTask::getBoardStatusId, projectTransferStatusBO.getTransferStatus().get(i)).set(ProjectTask::getStatus, statusType).update();
            boardStatusService.remove((new LambdaQueryWrapper<ProjectBoardStatus>().eq(ProjectBoardStatus::getStatusId, projectTransferStatusBO.getSourceStatus().get(i))));
//            boardStatusService.lambdaUpdate().eq(ProjectBoardStatus::getStatusId, projectTransferStatusBO.getSourceStatus().get(i)).
//                    set(ProjectBoardStatus::getStatusId, projectTransferStatusBO.getTransferStatus().get(i)).update();
        }
        //清楚无状态的看板
        schemeRelationBoardService.deleteByStatusCountEmpty();

        this.lambdaUpdate().eq(ProjectEventStatus::getUseStatus, 2).eq(ProjectEventStatus::getProjectEventId, projectTransferStatusBO.getEventId()).remove();
        this.lambdaUpdate().eq(ProjectEventStatus::getUseStatus, 0).eq(ProjectEventStatus::getProjectEventId, projectTransferStatusBO.getEventId()).
                set(ProjectEventStatus::getUseStatus, 1).update();

    }

    @Override
    public void updateInitStatus(Long eventId, Long eventStatusId) {
        ProjectEventStatus projectEventStatus = this.lambdaUpdate().getBaseMapper().selectById(eventStatusId);
        if (projectEventStatus.getUseStatus() != 1) {

            throw new BusinessException(ProjectCodeEnum.PROJECT_EVENT_STATUS_INIT_ERROR);

        }
        this.lambdaUpdate().eq(ProjectEventStatus::getProjectEventId, eventId).set(ProjectEventStatus::getInitStatus, 0).update();

        this.lambdaUpdate().eq(ProjectEventStatus::getProjectEventId, eventId).eq(ProjectEventStatus::getId, eventStatusId)
                .set(ProjectEventStatus::getInitStatus, 1).update();
    }

    @Override
    public ProjectEventStatus queryEventStatusByStatusName(String statusName, Integer eventId,Long projectId) {
        return this.baseMapper.queryEventStatusByStatusName(statusName, eventId,projectId);
    }

    @Override
    public ProjectEventStatus queryInitEventStatusByTaskType(Integer taskType, Long projectId) {
        Long eventId = taskType.longValue() - 1;
        return this.lambdaQuery().eq(ProjectEventStatus::getProjectEventId, eventId).eq(ProjectEventStatus::getInitStatus, 1)
                .eq(ProjectEventStatus::getProjectId, projectId).one();
    }
}
