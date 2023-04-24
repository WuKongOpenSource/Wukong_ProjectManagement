package com.kakarote.work.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.work.common.project.Const;
import com.kakarote.work.entity.PO.AdminRoleMenu;
import com.kakarote.work.mapper.ProjectRoleMenuMapper;
import com.kakarote.work.service.IProjectRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色菜单对应关系表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@Service
public class ProjectRoleMenuServiceImpl extends BaseServiceImpl<ProjectRoleMenuMapper, AdminRoleMenu> implements IProjectRoleMenuService {

    @Override
    public void saveRoleMenu(Long roleId, List<Long> menuIdList) {
        List<AdminRoleMenu> adminRoleMenuList = new ArrayList<>();
        menuIdList.forEach(menuId -> {
            AdminRoleMenu adminRoleMenu = new AdminRoleMenu();
            adminRoleMenu.setMenuId(menuId);
            adminRoleMenu.setRoleId(roleId);
            adminRoleMenuList.add(adminRoleMenu);
        });
        saveBatch(adminRoleMenuList, Const.BATCH_SAVE_SIZE);
    }
}
