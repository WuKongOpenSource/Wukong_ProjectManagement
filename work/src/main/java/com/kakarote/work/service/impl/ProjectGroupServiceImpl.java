package com.kakarote.work.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.constant.GroupTypeEnum;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectGroup;
import com.kakarote.work.entity.PO.ProjectGroupManagement;
import com.kakarote.work.entity.PO.ProjectUser;
import com.kakarote.work.mapper.ProjectGroupMapper;
import com.kakarote.work.service.IProjectGroupManagementService;
import com.kakarote.work.service.IProjectGroupService;
import com.kakarote.work.service.IProjectService;
import com.kakarote.work.service.IProjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目分组表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-09
 */
@Service
public class ProjectGroupServiceImpl extends BaseServiceImpl<ProjectGroupMapper, ProjectGroup> implements IProjectGroupService {

    @Autowired
    IProjectService projectService;
    @Autowired
    IProjectUserService projectUserService;
    @Autowired
    IProjectGroupManagementService projectGroupManagementService;

    @Override
    public void addGroup(ProjectGroup projectGroup) {
        projectGroup.setCreateUserId(UserUtil.getUserId());
        projectGroup.setType(GroupTypeEnum.CUSTOM.getType());
        save(projectGroup);
    }

    @Override
    public void updateGroup(ProjectGroup projectGroup) {
        projectGroup.setUpdateUserId(UserUtil.getUserId());
        updateById(projectGroup);
    }

    @Override
    public void updateGroupBatch(List<ProjectGroup> projectGroups) {
        if (CollectionUtil.isNotEmpty(projectGroups)) {
            projectGroups.stream().forEach(p -> {
                updateById(p);
            });
        }
    }

    @Override
    public List<ProjectGroup> searchGroupList() {
        //查询当前人的的分组信息
        LambdaQueryWrapper<ProjectGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectGroup::getCreateUserId,UserUtil.getUserId());
        List<ProjectGroup> list = list(queryWrapper).stream().sorted(Comparator.comparing(ProjectGroup::getSort, Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());
        //如果没有添加默认分组
        if(CollectionUtil.isEmpty(list)){
            //增加全部项目,未分组 两个默认分组，这两个分组不需要绑定项目，用途是页面分组排序
            this.saveBatch(this.getDefultGroup());
        }else{
            //查询是否有全部项目，未分组项目，如果没有则添加，主要用途是页面排序
            List<ProjectGroup> defualt = list.stream().filter(item -> (ObjectUtil.isNotEmpty(item.getType()) && item.getType().equals(GroupTypeEnum.ALL.getType()))
                    || (ObjectUtil.isNotEmpty(item.getType()) && item.getType().equals(GroupTypeEnum.NONE.getType()))).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(defualt)){
                List<ProjectGroup> defultGroups = getDefultGroup();
                this.saveBatch(defultGroups);
            }
        }

        //获取全部分组
        list = list(queryWrapper).stream().sorted(Comparator.comparing(ProjectGroup::getSort, Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            Integer allCount;
            Integer noneCount;
            //查询我参加的项目
            List<Long> projectIds = projectUserService.lambdaQuery().eq(ProjectUser::getUserId, UserUtil.getUserId()).list().stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(projectIds)) {
                LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.in(Project::getProjectId, projectIds);
                lambdaQueryWrapper.eq(Project::getStatus, 1);
                lambdaQueryWrapper.select(Project::getProjectId);
                //获取全部项目
                List<Project> projects = projectService.getBaseMapper().selectList(lambdaQueryWrapper);
                allCount = projects.size();
                //查询未分组的数据(分组根据根据个人自定义)
                List<Long> groupProjects = projectGroupManagementService.lambdaQuery().eq(ProjectGroupManagement::getCreateUserId,UserUtil.getUserId()).list().stream().map(p -> p.getProjectId()).collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(groupProjects)) {
                    List<Project> noneProjects = projects.stream().filter(f -> !groupProjects.contains(f.getProjectId())).distinct().collect(Collectors.toList());
                    noneCount = noneProjects.size();
                } else {
                    noneCount = 0;
                }
            } else {
                noneCount = 0;
                allCount = 0;
            }
            list.stream().forEach(l -> {
                //如果是全部项目分组查询所有项目
                if(ObjectUtil.isNotEmpty(l.getType()) && GroupTypeEnum.ALL.getType().equals(l.getType())){
                    l.setNum(allCount);
                }else if(ObjectUtil.isNotEmpty(l.getType()) && GroupTypeEnum.NONE.getType().equals(l.getType())){
                    //如果是未分组查询所有未分组项目
                    l.setNum(noneCount);
                }else{
                    List<Long> projects = projectGroupManagementService.lambdaQuery().eq(ProjectGroupManagement::getCreateUserId,UserUtil.getUserId()).eq(ProjectGroupManagement::getGroupId, l.getGroupId()).list()
                            .stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(projects)) {
                        l.setNum(projects.size());
                    }
                }

            });
        }
        return list;
    }

    @Override
    public void removeGroupById(Long groupId) {
        //删除分组关系
        projectGroupManagementService.lambdaUpdate().eq(ProjectGroupManagement::getCreateUserId,UserUtil.getUserId()).eq(ProjectGroupManagement::getGroupId, groupId).remove();
        //删除分组
        removeById(groupId);
    }

    public List<ProjectGroup> getDefultGroup() {
        List<ProjectGroup> list = new ArrayList<>();
        ProjectGroup allGroup = new ProjectGroup();
        allGroup.setName(GroupTypeEnum.ALL.getDesc());
        allGroup.setSort(0);
        allGroup.setType(GroupTypeEnum.ALL.getType());
        list.add(allGroup);

        ProjectGroup noreGroup = new ProjectGroup();
        noreGroup.setName(GroupTypeEnum.NONE.getDesc());
        noreGroup.setSort(1);
        noreGroup.setType(GroupTypeEnum.NONE.getType());
        list.add(noreGroup);
        return list;

    }


}
