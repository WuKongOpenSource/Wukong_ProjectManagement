package com.kakarote.work.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.AdminRole;

import java.util.List;

/**
 * <p>
 * 项目角色表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
public interface IProjectRoleService extends BaseService<AdminRole> {


    List<AdminRole> queryProjectRoleByTypes(List<Integer> types);

    /**
     * 功能描述: <br>
     * 〈查询项目管理成员的权限〉
     * @param roleIds
     * @return java.util.List<java.lang.Long>
     * @author zyh
     */
    List<Long> getAllRoleMenuId(List<Long> roleIds);

    /**
     * 查询项目管理成员的权限
     *
     * @param recordList
     * @return
     */
    List<AdminRole> getAllRoleMenu(List<AdminRole> recordList);

    JSONObject auth(Long userId);

    JSONObject getMenuListByType();

    List<AdminRole> getRoleByType();

    /**
     * 添加自定义项目角色和权限
     *
     * @param adminRole
     * @return
     */
    AdminRole addProjectRole(AdminRole adminRole);

    /**
     * 删除角色
     *
     * @param roleId roleId
     */
    public void delete(Long roleId);

    /**
     * 修改角色菜单关系
     *
     * @param adminRole adminrole
     */
    public void updateRoleMenu(AdminRole adminRole);

    /**
     * 查询项目管理角色
     *
     * @return
     */
    AdminRole queryDefaultRole();
}
