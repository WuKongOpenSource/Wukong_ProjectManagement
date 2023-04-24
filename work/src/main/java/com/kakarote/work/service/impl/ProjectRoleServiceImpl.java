package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.RecursionUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.PO.AdminMenu;
import com.kakarote.work.common.admin.AdminMenuVO;
import com.kakarote.work.common.project.BaseUtil;
import com.kakarote.work.constant.ProjectCodeEnum;
import com.kakarote.work.entity.PO.AdminRole;
import com.kakarote.work.mapper.ProjectRoleMapper;
import com.kakarote.work.service.IProjectMenuService;
import com.kakarote.work.service.IProjectRoleMenuService;
import com.kakarote.work.service.IProjectRoleService;
import com.kakarote.work.service.IProjectUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 事件绑定属性表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-20
 */
@Service
public class ProjectRoleServiceImpl extends BaseServiceImpl<ProjectRoleMapper, AdminRole> implements IProjectRoleService {

    @Autowired
    private IProjectMenuService projectMenuService;

    @Autowired
    private IProjectRoleMenuService projectRoleMenuService;

    @Autowired
    private IProjectUserRoleService projectUserRoleService;

    private static final String CP = "cp";

    /**
     * 默认角色的菜单ID
     */
    public static final List<Long> ROLE_MENU_LIST_8 = Arrays.asList(310L, 311L, 312L, 313L, 314L, 315L, 316L, 317L, 318L, 319L, 320L, 321L, 322L, 323L, 324L, 325L, 326L, 327L, 328L, 329L, 330L, 331L, 332L, 333L, 334L, 335L, 336L, 337L, 927L);


    @Override
    public List<AdminRole> queryProjectRoleByTypes(List<Integer> types) {
        List<AdminRole> adminRoles = new ArrayList<>();
        List<AdminRole> roles = this.lambdaQuery().eq(AdminRole::getRoleType, 8).in(AdminRole::getLabel, types).list();
        if (CollectionUtil.isNotEmpty(roles)) {
            roles.stream().forEach(r -> {
                AdminRole adminRole = new AdminRole();
                BeanUtil.copyProperties(r, adminRole);
                adminRoles.add(adminRole);
            });
        }
        return adminRoles;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @param roleIds
     * @return java.util.List<java.lang.Long>
     * @author zyh
     */
    @Override
    public List<Long> getAllRoleMenuId(List<Long> roleIds) {
        List<AdminRole> adminRoles = null;
        List<Long> roles = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(roleIds)) {
            adminRoles = this.lambdaQuery().in(AdminRole::getRoleId, roleIds).list();
        }
        List<AdminRole> roleByType = null;
        if (ObjectUtil.isNotEmpty(adminRoles)) {
            roleByType = this.getAllRoleMenu(adminRoles);
            List<List<Long>> rules = roleByType.stream().map(r -> r.getRules().get("data")).collect(Collectors.toList());
            for (List<Long> list : rules) {
                roles.addAll(list);
            }
        }
        return roles;
    }

    @Override
    public List<AdminRole> getAllRoleMenu(List<AdminRole> recordList) {
        String realm = "project";
        LambdaQueryWrapper<AdminMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(AdminMenu::getMenuId);
        wrapper.eq(AdminMenu::getParentId, 0L);
        wrapper.eq(AdminMenu::getRealm, realm);
        AdminMenu adminMenu = projectMenuService.getOne(wrapper);
        if (adminMenu != null) {
            Long pid = projectMenuService.getOne(wrapper).getMenuId();
            if (recordList.size() != 0) {
                recordList.forEach(record -> {
                    Map<String, List<Long>> map = new HashMap<>();
                    List<Long> data = getBaseMapper().getRoleMenu(pid, record.getRoleId());
                    map.put("data", data);
                    Map<String, String> keyMap = new HashMap<>();
                    keyMap.put("roleName_resourceKey", "rolesChild" + record.getRoleType() + "." + record.getRoleName());
                    record.setLanguageKeyMap(keyMap);
                    record.setRules(map);
                });
            }
        }
        return recordList;
    }

    /**
     * 根据类型查询角色
     *
     * @return data
     */
    @Override
    public List<AdminRole> getRoleByType() {
        List<AdminRole> recordList = lambdaQuery().eq(AdminRole::getRoleType, 8).list();
        String realm = "project";
        LambdaQueryWrapper<AdminMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(AdminMenu::getMenuId);
        wrapper.eq(AdminMenu::getParentId, 0L);
        wrapper.eq(AdminMenu::getRealm, realm);
        AdminMenu adminMenu = projectMenuService.getOne(wrapper);
        if (adminMenu != null) {
            Long pid = projectMenuService.getOne(wrapper).getMenuId();
            if (recordList.size() != 0) {
                recordList.forEach(record -> {
                    Map<String, List<Long>> map = new HashMap<>();
                    List<Long> data = getBaseMapper().getRoleMenu(pid, record.getRoleId());
                    map.put("data", data);
                    record.setRules(map);
                });
            }
        }
        return recordList;
    }

    @Override
    public JSONObject auth(Long userId) {
        //临时模拟接口，无作用，只是减少前端更改变通一下
        JSONObject word = new JSONObject();
        JSONObject project = new JSONObject();
        JSONObject data = new JSONObject();
        project.put("addChildTask",true);
        project.put("archiveTask",true);
        project.put("cleanTask",true);
        project.put("deleteChildTask",true);
        project.put("deleteTask",true);
        project.put("deleteTaskClass",true);
        project.put("deleteTaskFile",true);
        project.put("excelExport",true);
        project.put("excelImport",true);
        project.put("manageTaskOwnerUser",true);
        project.put("restoreTask",true);
        project.put("saveTask",true);
        project.put("saveTaskClass",true);
        project.put("saveTaskRelation",true);
        project.put("setChildTaskStatus",true);
        project.put("setTaskDescription",true);
        project.put("setTaskLabel",true);
        project.put("setTaskMainUser",true);
        project.put("setTaskOrder",true);
        project.put("setTaskOwnerUser",true);
        project.put("setTaskPriority",true);
        project.put("setTaskStatus",true);
        project.put("setTaskTime",true);
        project.put("setTaskTitle",true);
        project.put("setWork",true);
        project.put("updateChildTask",true);
        project.put("updateClassOrder",true);
        project.put("updateTaskClass",true);
        project.put("uploadTaskFile",true);
        data.put("project",project);
        word.put("work",data);
        return word;
    }

    @Override
    public JSONObject getMenuListByType() {
        JSONObject object = new JSONObject();
        String realm = "project";
        LambdaQueryWrapper<AdminMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminMenu::getParentId, 0L);
        wrapper.eq(AdminMenu::getRealm, realm);
        AdminMenu menu = projectMenuService.getOne(wrapper);
        AdminMenuVO data = BeanUtil.copyProperties(menu, AdminMenuVO.class);
        List<AdminMenuVO> menuList = getMenuList(data.getMenuId());
        //升级删除项目和进销存  目前在代码中处理防止需求变更
//        menuList.removeIf(adminMenuVO -> "work".equals(adminMenuVO.getRealm()));
        data.setChildMenu(menuList);
        object.put("data", data);
        return object;
    }

    @Override
    public AdminRole addProjectRole(AdminRole adminRole) {
        List<Long> adminRoleMenuIds = adminRole.getMenuIds();
        String remark = adminRole.getRemark();
        Long count = lambdaQuery().eq(AdminRole::getRoleName, adminRole.getRoleName()).eq(AdminRole::getRoleType, adminRole.getRoleType()).count();
        if (count > 0) {
            throw new BusinessException(ProjectCodeEnum.PROJECT_ROLE_NAME_EXIST_ERROR);
        }
        if (adminRole.getRoleId() != null) {
            updateById(adminRole);
        } else {
            adminRole.setRoleId(null);
            adminRole.setStatus(1);
            adminRole.setRemark(null);
            save(adminRole);
        }
        adminRole.setRemark(remark);
        adminRole.setMenuIds(adminRoleMenuIds);
        this.updateRoleMenu(adminRole);
        return adminRole;
    }

    /**
     * 修改角色菜单关系
     *
     * @param adminRole adminrole
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRoleMenu(AdminRole adminRole) {
        updateById(adminRole);
        projectRoleMenuService.removeByMap(Collections.singletonMap("role_id", adminRole.getRoleId()));
        projectRoleMenuService.saveRoleMenu(adminRole.getRoleId(), adminRole.getMenuIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long roleId) {
        AdminRole adminRole = getById(roleId);
        if (CP.equals(adminRole.getRemark())) {
            throw new BusinessException(ProjectCodeEnum.PROJECT_DEFAULT_ROLE_CANNOT_BE_DELETED);
        }
        removeById(roleId);
        JSONObject object = new JSONObject().fluentPut("role_id", roleId);
        projectUserRoleService.removeByMap(object);
        projectRoleMenuService.removeByMap(object);
    }

    private List<AdminMenuVO> getMenuList(Long parentId, String... notRealm) {
        LambdaQueryWrapper<AdminMenu> chainWrapper = new LambdaQueryWrapper<>();
        if (notRealm.length > 0) {
            chainWrapper.notIn(AdminMenu::getRealm, Arrays.asList(notRealm));
        }
        chainWrapper.orderByAsc(AdminMenu::getSort);
        List<AdminMenu> list = projectMenuService.list(chainWrapper);
        return RecursionUtil.getChildListTree(list, "parentId", parentId, "menuId", "childMenu", AdminMenuVO.class);
    }

    @Override
    public AdminRole queryDefaultRole() {
        List<AdminRole> list = lambdaQuery().eq(AdminRole::getRoleName, "默认角色").eq(AdminRole::getRemark, "cp").list();
        Map<String, String> keymap = new HashMap<>();
        keymap.put("roleName_resourceKey", "rolesChild1.默认角色");
        if (list.isEmpty()) {
            Long userId = UserUtil.getUserId();
            LocalDateTime dateTime = LocalDateTime.now();
            AdminRole adminRole = new AdminRole(BaseUtil.getNextId(), "默认角色", 1, "cp", 1, 2, 1, 5,  userId, userId, dateTime, dateTime);
            save(adminRole);
            projectRoleMenuService.saveRoleMenu(adminRole.getRoleId(), ROLE_MENU_LIST_8);
            adminRole.setLanguageKeyMap(keymap);
            return adminRole;
        }
        return list.get(0).setLanguageKeyMap(keymap);
    }

}
