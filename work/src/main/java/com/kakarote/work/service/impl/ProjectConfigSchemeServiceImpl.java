package com.kakarote.work.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.PO.ProjectConfigScheme;
import com.kakarote.work.entity.PO.ProjectEvent;
import com.kakarote.work.entity.PO.ProjectSchemeRelation;
import com.kakarote.work.mapper.ProjectConfigSchemeMapper;
import com.kakarote.work.service.IProjectConfigSchemeService;
import com.kakarote.work.service.IProjectEventService;
import com.kakarote.work.service.IProjectSchemeRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目配置方案表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@Service
public class ProjectConfigSchemeServiceImpl extends BaseServiceImpl<ProjectConfigSchemeMapper, ProjectConfigScheme> implements IProjectConfigSchemeService {

    @Autowired
    IProjectEventService projectEventService;
    @Autowired
    IProjectSchemeRelationService projectSchemeRelationService;


    @Override
    public void add(ProjectConfigScheme projectConfigScheme) {
        String uuid = projectConfigScheme.getBatchId();
        if (StrUtil.isNotEmpty(uuid)) {
            uuid = IdUtil.simpleUUID();
        }
        projectConfigScheme.setCreateUserId(UserUtil.getUserId());
        save(projectConfigScheme);
        //增加默认绑定事件,1 敏捷开发包含(需求、任务、缺陷)  2普工开发包含(任务)
        //1需求 2任务 3缺陷 4子工作项目
        List<Integer> types = Arrays.asList(1, 2, 3, 4);
        List<ProjectEvent> projectEvents = null;
        if (1 == projectConfigScheme.getCollaborationType()) {
            projectEvents = projectEventService.lambdaQuery().in(ProjectEvent::getType, Arrays.asList(5)).list();
        } else {
            projectEvents = projectEventService.lambdaQuery().in(ProjectEvent::getType, types).list();
        }
        String finalUuid = uuid;
        if (ObjectUtil.isNotEmpty(projectEvents)) {
            projectEvents.forEach(p -> {
                ProjectSchemeRelation projectSchemeRelation = new ProjectSchemeRelation();
                projectSchemeRelation.setBatchId(finalUuid);
                projectSchemeRelation.setCreateUserId(UserUtil.getUserId());
                projectSchemeRelation.setEventId(p.getId());
                projectSchemeRelation.setSchemeId(projectConfigScheme.getId());
                projectSchemeRelation.setType(p.getType());
                projectSchemeRelation.setWorkflowId(p.getWorkflowId());
                projectSchemeRelationService.save(projectSchemeRelation);
            });
        }
    }

    @Override
    public void update(ProjectConfigScheme projectConfigScheme) {
        projectConfigScheme.setUpdateUserId(UserUtil.getUserId());
        update(projectConfigScheme);
    }

    @Override
    public void delete(Long schemeId) {
        //删除事件关联表数据
        projectSchemeRelationService.lambdaUpdate().eq(ProjectSchemeRelation::getSchemeId, schemeId).remove();
        removeById(schemeId);
    }

    @Override
    public List<ProjectEvent> queryBySchemeId(Long schemeId) {
        List<ProjectSchemeRelation> projectSchemeRelationList = projectSchemeRelationService.lambdaQuery().eq(ProjectSchemeRelation::getSchemeId, schemeId).list();
        List<Long> ids = null;
        if (CollectionUtil.isNotEmpty(projectSchemeRelationList)) {
            ids = projectSchemeRelationList.stream().map(p -> p.getEventId()).collect(Collectors.toList());
        }
        return projectEventService.lambdaQuery().in(ProjectEvent::getId, ids).list();
    }
}
