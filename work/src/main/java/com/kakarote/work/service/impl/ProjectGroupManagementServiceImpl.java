package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectGroupManagement;
import com.kakarote.work.mapper.ProjectGroupManagementMapper;
import com.kakarote.work.service.IProjectGroupManagementService;
import com.kakarote.work.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目分组管理表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-17
 */
@Service
public class ProjectGroupManagementServiceImpl extends BaseServiceImpl<ProjectGroupManagementMapper, ProjectGroupManagement> implements IProjectGroupManagementService {

    @Autowired
    IProjectService projectService;

    @Override
    public void moveToGroup(ProjectGroupManagement projectGroupManagement) {
        List<ProjectGroupManagement> currentGroups = lambdaQuery().eq(ProjectGroupManagement::getProjectId, projectGroupManagement.getProjectId()).eq(ProjectGroupManagement::getCreateUserId, UserUtil.getUserId()).list();
        ProjectGroupManagement currentGroup ;
        if(CollectionUtil.isNotEmpty(currentGroups)){
            currentGroup = CollUtil.getFirst(currentGroups);
            currentGroup.setGroupId(projectGroupManagement.getGroupId());
        }else{
            currentGroup = BeanUtil.copyProperties(projectGroupManagement, ProjectGroupManagement.class);
            //确保误传id造成分组失效
            currentGroup.setId(null);
        }
        currentGroup.setBatchId(IdUtil.simpleUUID());
        saveOrUpdate(currentGroup);
    }

    @Override
    public void removeToGroup(Long groupId, Long projectId) {
        lambdaUpdate().eq(ProjectGroupManagement::getGroupId, groupId).eq(ProjectGroupManagement::getProjectId, projectId).eq(ProjectGroupManagement::getCreateUserId,UserUtil.getUserId()).remove();
    }

    @Override
    public List<Project> searchProjectGroupList(Long groupId) {
        List<ProjectGroupManagement> groupIdList = lambdaQuery().eq(ProjectGroupManagement::getGroupId, groupId).eq(ProjectGroupManagement::getCreateUserId,UserUtil.getUserId()).list();
        List<Long> projectIds = null;
        if (CollectionUtil.isNotEmpty(groupIdList)) {
            projectIds = groupIdList.stream().map(g -> g.getProjectId()).collect(Collectors.toList());
            return projectService.lambdaQuery().in(Project::getProjectId, projectIds).list();
        }
        return null;
    }
}
