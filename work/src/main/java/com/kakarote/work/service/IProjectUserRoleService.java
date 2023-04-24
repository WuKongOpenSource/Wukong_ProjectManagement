package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.AdminUserRole;

import java.util.List;

/**
 * <p>
 * 用户角色对应关系表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface IProjectUserRoleService extends BaseService<AdminUserRole> {

    /**
     * 通过userID删除该用户的所有
     * @param userId 用户ID
     * @param isRemove 是否删除原有角色
     * @param roleId 角色列表
     */
    public void saveByUserId(Long userId, boolean isRemove, List<String> roleId);

    /**
     * 添加默认角色
     * @param userId 用户ID
     */
    public void saveByUserId(Long userId);
}
