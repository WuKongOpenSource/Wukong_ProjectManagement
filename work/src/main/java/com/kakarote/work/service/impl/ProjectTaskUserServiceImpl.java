package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.common.project.ProjectUtil;
import com.kakarote.work.entity.BO.ProjectTaskUserBO;
import com.kakarote.work.entity.PO.ProjectTaskUser;
import com.kakarote.work.entity.PO.ProjectUser;
import com.kakarote.work.mapper.ProjectTaskUserMapper;
import com.kakarote.work.service.IProjectTaskLogService;
import com.kakarote.work.service.IProjectTaskUserService;
import com.kakarote.work.service.IProjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目成员表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
@Service
public class ProjectTaskUserServiceImpl extends BaseServiceImpl<ProjectTaskUserMapper, ProjectTaskUser> implements IProjectTaskUserService {


    @Autowired
    private IProjectUserService projectUserService;

    @Autowired
    private IProjectTaskLogService projectTaskLogService;


    @Override
    public void relatedProjectUser(ProjectTaskUserBO projectTaskUserBO) {
        if( ObjectUtil.isEmpty(projectTaskUserBO.getProjectId())
           ||ObjectUtil.isEmpty(projectTaskUserBO.getTaskId())
           || CollectionUtil.isEmpty(projectTaskUserBO.getUserIds())){
            return;
        }

        //查询项目中的成员
        List<ProjectUser> projectUsers = projectUserService.lambdaQuery()
                .eq(ProjectUser::getProjectId, projectTaskUserBO.getProjectId())
                .in(ProjectUser::getUserId, projectTaskUserBO.getUserIds())
                .list();
        if(CollectionUtil.isEmpty(projectUsers)){
            return;
        }
        //查询原有任务成员
        LambdaQueryWrapper<ProjectTaskUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectTaskUser::getProjectId, projectTaskUserBO.getProjectId())
                .eq(ProjectTaskUser::getTaskId, projectTaskUserBO.getTaskId());
        List<ProjectTaskUser> oldUserList = this.list(queryWrapper);
        //删除原有的任务成员
        LambdaUpdateWrapper<ProjectTaskUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectTaskUser::getProjectId, projectTaskUserBO.getProjectId())
                     .eq(ProjectTaskUser::getTaskId, projectTaskUserBO.getTaskId());
        this.remove(updateWrapper);
        //添加任务成员
        List<ProjectTaskUser> projectTaskUsers = projectUsers.stream()
                .map(item -> {
                    ProjectTaskUser projectTaskUser = BeanUtil.copyProperties(item, ProjectTaskUser.class);
                    projectTaskUser.setId(null);
                    projectTaskUser.setTaskId(projectTaskUserBO.getTaskId());
                    return projectTaskUser;
                })
                .collect(Collectors.toList());
        saveBatch(projectTaskUsers);
        String contentLog = "";
        String oldUser = "";
        String newUser = "";
        //原有成员与现有成员进行对比
        if(CollectionUtil.isNotEmpty(oldUserList)){
            List<Long> oldUserIds = oldUserList.stream().map(ProjectTaskUser::getUserId).collect(Collectors.toList());
            List<SimpleUser> simpleUsers = UserCacheUtil.getSimpleUsers(oldUserIds);
            if(CollectionUtil.isNotEmpty(simpleUsers)){
                oldUser = simpleUsers.stream().map(SimpleUser::getNickname).filter(StrUtil::isNotBlank).collect(Collectors.joining(","));
            }
        }
        if(CollectionUtil.isNotEmpty(projectTaskUserBO.getUserIds())){
            List<Long> newUserIds = projectTaskUserBO.getUserIds();
            List<SimpleUser> simpleUsers = UserCacheUtil.getSimpleUsers(newUserIds);
            if(CollectionUtil.isNotEmpty(simpleUsers)){
                newUser = simpleUsers.stream().map(SimpleUser::getNickname).filter(StrUtil::isNotBlank).collect(Collectors.joining(","));
            }
        }
        if(ObjectUtil.isNotEmpty(oldUser) && !oldUser.equals(newUser)){
            contentLog = ProjectUtil.getLogContent("团队成员", oldUser, newUser);
        }
        if(StrUtil.isNotBlank(contentLog)){
            projectTaskLogService.saveTaskLog(projectTaskUserBO.getTaskId(), contentLog);
        }
    }

}
