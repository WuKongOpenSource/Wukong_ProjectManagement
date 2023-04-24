package com.kakarote.work.service;

import com.alibaba.fastjson.JSONObject;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.common.admin.AdminProjectRole;
import com.kakarote.work.common.admin.AdminProjectRoleBO;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.common.admin.AdminEditProjectRoleBO;
import com.kakarote.work.entity.BO.ProjectRoleQueryBO;
import com.kakarote.work.entity.PO.ProjectUser;
import com.kakarote.work.entity.VO.ProjectRolesGroupVO;

import java.util.List;

/**
 * <p>
 * 项目成员表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
public interface IProjectUserService extends BaseService<ProjectUser> {

    void relatedProjectUser(List<AdminProjectRoleBO> adminProjectRoleBOS);

    void editProjectUser(AdminEditProjectRoleBO adminEditProjectRoleBO);

    List<Long> getAllRoleMenu(Long projectId, Long userId);

    List<ProjectRolesGroupVO> getProjectRoles(Long projectId);

    void deleteProjectRoles(AdminProjectRole adminProjectRole);

    List<ProjectOwnerRoleBO> queryProjectUser(ProjectRoleQueryBO projectRoleQueryBO);
    List<Long> queryMyProjectIds();
    public List<String> queryProjectAdminUser(Long projectId);


    /**
     * 根据项目id查询项目权限
     * @param projectId
     * @return
     */
    JSONObject getProjectAuth(Long projectId);

    /**
     * 批量查询项目权限
     * @param projectIds
     * @return
     */
    List<JSONObject> projectAuthList( List<Long> projectIds);
}
