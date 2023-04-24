package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.AdminRoleMenu;

import java.util.List;

/**
 * <p>
 * 角色菜单对应关系表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface IProjectRoleMenuService extends BaseService<AdminRoleMenu> {
    /**
     * 保存角色枚举
     *
     * @param roleId:角色id
     * @param menuIdList:menuIdList
     * @return
     */
    public void saveRoleMenu(Long roleId, List<Long> menuIdList);
}
