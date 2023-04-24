package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.exception.BusinessException;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.entity.PO.AdminMenu;
import com.kakarote.work.common.admin.AdminProjectRole;
import com.kakarote.work.common.admin.AdminProjectRoleBO;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.constant.ProjectCodeEnum;
import com.kakarote.work.common.admin.AdminEditProjectRoleBO;
import com.kakarote.work.entity.PO.AdminRole;
import com.kakarote.work.entity.BO.ProjectRoleQueryBO;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectTaskUser;
import com.kakarote.work.entity.PO.ProjectUser;
import com.kakarote.work.entity.VO.ProjectRolesGroupVO;
import com.kakarote.work.mapper.ProjectUserMapper;
import com.kakarote.work.service.IProjectRoleService;
import com.kakarote.work.service.IProjectService;
import com.kakarote.work.service.IProjectTaskUserService;
import com.kakarote.work.service.IProjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
public class ProjectUserServiceImpl extends BaseServiceImpl<ProjectUserMapper, ProjectUser> implements IProjectUserService {


    @Autowired
    private IProjectRoleService projectRoleService;

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectTaskUserService projectTaskUserService;


    @Override
    public void relatedProjectUser(List<AdminProjectRoleBO> adminProjectRoleBOS) {
        adminProjectRoleBOS.stream().forEach(u -> {
            u.getRoleIds().stream().forEach(r -> {
                ProjectUser projectUser = new ProjectUser();
                projectUser.setProjectId(u.getProjectId());
                projectUser.setUserId(u.getUserId());
                projectUser.setRoleId(r);
                saveOrUpdate(projectUser);
            });
        });
    }

    @Override
    public void editProjectUser(AdminEditProjectRoleBO adminEditProjectRoleBO) {
        lambdaUpdate().eq(ProjectUser::getProjectId, adminEditProjectRoleBO.getProjectId()).eq(ProjectUser::getUserId, adminEditProjectRoleBO.getUserId()).remove();
        adminEditProjectRoleBO.getRoleIds().stream().forEach(r -> {
            ProjectUser projectUser = new ProjectUser();
            projectUser.setProjectId(adminEditProjectRoleBO.getProjectId());
            projectUser.setUserId(adminEditProjectRoleBO.getUserId());
            projectUser.setRoleId(r);
            saveOrUpdate(projectUser);
        });
    }

    @Override
    public List<Long> getAllRoleMenu(Long projectId, Long userId) {
        List<Long> roles = null;
        List<ProjectUser> projectUsers = lambdaQuery().eq(ProjectUser::getProjectId, projectId).eq(ProjectUser::getUserId, userId).list();
        if (CollectionUtil.isNotEmpty(projectUsers)) {
            List<Long> roleIds = projectUsers.stream().map(m -> m.getRoleId()).collect(Collectors.toList());
            roles = projectRoleService.getAllRoleMenuId(roleIds);
        }
        return roles;
    }

    @Override
    public List<ProjectRolesGroupVO> getProjectRoles(Long projectId) {
        List<ProjectRolesGroupVO> projectRolesGroupVOS = getBaseMapper().queryRolesRelation(projectId);
        if (CollectionUtil.isNotEmpty(projectRolesGroupVOS)) {
            projectRolesGroupVOS.stream().forEach(p -> {
                p.setRules(projectRoleService.getAllRoleMenuId(Arrays.asList(p.getRoleId())));
            });
        }
        return projectRolesGroupVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectRoles(AdminProjectRole adminProjectRole) {
        //查询管理员ID
        List<AdminRole> adminRoles = projectRoleService.queryProjectRoleByTypes(Arrays.asList(1));
        List<Long> ids = adminRoles.stream().map(m -> m.getRoleId()).collect(Collectors.toList());
        //检查其他成员是否包含项目管理员角色 包含则直接删除当前。否则不能删除
        List<ProjectUser> projectUsers = lambdaQuery().eq(ProjectUser::getProjectId, adminProjectRole.getProjectId()).ne(ProjectUser::getUserId, adminProjectRole.getUserId()).list();
        if (ObjectUtil.isNotEmpty(projectUsers)) {
            for (ProjectUser projectUser : projectUsers) {
                if (ids.contains(projectUser.getRoleId())) {
                    lambdaUpdate().eq(ProjectUser::getProjectId, adminProjectRole.getProjectId()).eq(ProjectUser::getUserId, adminProjectRole.getUserId()).remove();
                    return;
                }
            }
        }
        ProjectRoleQueryBO projectRoleQueryBO = new ProjectRoleQueryBO();
        projectRoleQueryBO.setProjectId(adminProjectRole.getProjectId());
        List<ProjectOwnerRoleBO> users = queryProjectUser(projectRoleQueryBO);
        if (users.size() == 1) {
            throw new BusinessException(ProjectCodeEnum.PROJECT_UNION_PROJECT_ADMIN_ERROR);
        }
    }

    @Override
    public List<ProjectOwnerRoleBO> queryProjectUser(ProjectRoleQueryBO projectRoleQueryBO) {
        Project project = projectService.getById(projectRoleQueryBO.getProjectId());
        if(ObjectUtil.isEmpty(project)){
            return new ArrayList<>();
        }
        Map<Long, Set<Long>> users = new HashMap<>();
        //如果不存在taskID
        if(ObjectUtil.isEmpty(projectRoleQueryBO.getTaskId())){
            List<ProjectUser> projectUsers = lambdaQuery().select().eq(ProjectUser::getProjectId, projectRoleQueryBO.getProjectId()).list();
            users = projectUsers.stream().collect(Collectors.groupingBy(ProjectUser::getUserId, Collectors.mapping(ProjectUser::getRoleId, Collectors.toSet())));
        }else{
            List<ProjectTaskUser> projectTaskUsers = projectTaskUserService.lambdaQuery()
                    .eq(ProjectTaskUser::getProjectId, projectRoleQueryBO.getProjectId())
                    .eq(ProjectTaskUser::getTaskId, projectRoleQueryBO.getTaskId())
                    .list();
            users = projectTaskUsers.stream().collect(Collectors.groupingBy(ProjectTaskUser::getUserId, Collectors.mapping(ProjectTaskUser::getRoleId, Collectors.toSet())));
        }
        List<ProjectOwnerRoleBO> ownerRoleBOS = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(users)) {
            for (Long userId : users.keySet()) {
                ProjectOwnerRoleBO projectOwnerRoleBO = new ProjectOwnerRoleBO();
                projectOwnerRoleBO.setUserId(userId);
                projectOwnerRoleBO.setRealname(UserCacheUtil.getUserName(userId));
                if(project.getCreateUserId().equals(userId)){
                    projectOwnerRoleBO.setIsPmCreater(1);
                }else{
                    projectOwnerRoleBO.setIsPmCreater(0);
                }
                Set<Long> roleIds = users.get(userId);
                String userName = UserCacheUtil.getUserName(userId);
                List<AdminRole> adminRoles = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(roleIds)) {
                    roleIds.forEach(roleId -> {
                        AdminRole adminRole = projectRoleService.getById(roleId);
                        if (ObjectUtil.isNotEmpty(adminRole)) {
                            if (ObjectUtil.isNotEmpty(projectRoleQueryBO.getRoleId()) && ObjectUtil.isEmpty(projectRoleQueryBO.getUserName())) {
                                if (ObjectUtil.equal(adminRole.getRoleId(), projectRoleQueryBO.getRoleId())) {
                                    projectOwnerRoleBO.setLabelProject(1);
                                }
                            } else if (ObjectUtil.isNotEmpty(projectRoleQueryBO.getUserName()) && ObjectUtil.isEmpty(projectRoleQueryBO.getRoleId())) {
                                if (ObjectUtil.equal(userName, projectRoleQueryBO.getUserName())) {
                                    projectOwnerRoleBO.setLabelProject(1);
                                }
                            } else if (ObjectUtil.isNotEmpty(projectRoleQueryBO.getRoleId()) && ObjectUtil.isNotEmpty(projectRoleQueryBO.getUserName())) {
                                if (ObjectUtil.equal(adminRole.getRoleId(), projectRoleQueryBO.getRoleId()) && ObjectUtil.equal(userName, projectRoleQueryBO.getUserName())) {
                                    projectOwnerRoleBO.setLabelProject(1);
                                }
                            }
                            AdminRole adminRole1 = new AdminRole();
                            BeanUtil.copyProperties(adminRole, adminRole1);
                            adminRoles.add(adminRole1);
                        }
                    });
                    projectOwnerRoleBO.setAdminRoles(adminRoles);
                }
                ownerRoleBOS.add(projectOwnerRoleBO);
            }
        }
        if (ObjectUtil.isNotEmpty(projectRoleQueryBO.getRoleId()) || ObjectUtil.isNotEmpty(projectRoleQueryBO.getUserName())) {
            ownerRoleBOS = ownerRoleBOS.stream().filter(o -> ObjectUtil.equal(o.getLabelProject(), 1)).collect(Collectors.toList());
        }
        ownerRoleBOS.stream().forEach(o -> {
            o.setImg(UserCacheUtil.getUserInfo(o.getUserId()).getUserImg());
        });
        return ownerRoleBOS;
    }

    @Override
    public List<Long> queryMyProjectIds() {
        Long userId= UserUtil.getUserId();
        return this.baseMapper.queryMyProjectIds(userId);
    }

    @Override
    public List<String> queryProjectAdminUser(Long projectId) {
        List<Long> adminUserIds = this.baseMapper.queryProjectAdminUser(projectId);
        if(CollectionUtil.isNotEmpty(adminUserIds)){
            List<SimpleUser> adminUser = UserCacheUtil.getSimpleUsers(adminUserIds);
            if(CollectionUtil.isNotEmpty(adminUser)){
                return adminUser.stream().map(SimpleUser::getNickname).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    /**
     * 功能描述: <br>
     * 〈查询是否拥有权限〉
     * @param projectId
     * @return com.alibaba.fastjson.JSONObject
     * @author zyh
     */
    @Override
    public JSONObject getProjectAuth(Long projectId){
        List<AdminMenu> adminMenus = this.getBaseMapper().getProjectAuth(projectId,UserUtil.getUserId());
        JSONObject jsonObject = new JSONObject();
        adminMenus.forEach(item->{
            Boolean projectAuth = item.getProjectAuth();
            //如果是超级管理员
            if(UserUtil.isAdmin()){
                projectAuth = Boolean.TRUE;
            }
            jsonObject.put(item.getRealm(),projectAuth);
        });
        return jsonObject;
    };

    /**
     * 功能描述: <br>
     * 〈批量查询项目权限〉
     * @param projectIds
     * @return com.alibaba.fastjson.JSONObject
     * @author zyh
     */
    @Override
    public List<JSONObject> projectAuthList(List<Long> projectIds) {
        List<JSONObject> projectAuthList=new ArrayList<>();
        for (Long projectId : projectIds) {
            projectAuthList.add( this.getProjectAuth(projectId));

        }
        return projectAuthList;
    }
}
