package com.kakarote.work.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.AdminMenu;
import com.kakarote.work.entity.PO.ProjectUser;
import com.kakarote.work.entity.VO.ProjectRolesGroupVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目成员表 Mapper 接口
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
public interface ProjectUserMapper extends BaseMapper<ProjectUser> {

    //查询角色关联用户数目
    List<ProjectRolesGroupVO> queryRolesRelation(@Param("projectId") Long projectId);
    //查询用户的可访问
    List<Long> queryMyProjectIds(@Param("userId") Long userId);
    List<Long> queryProjectAdminUser(@Param("projectId") Long projectId);

    /**
     * 查询项目管理项目下角色菜单列表
     *
     * @param projectId    项目id
     * @param userId    人员id
     * @return menus
     */
    @InterceptorIgnore(tenantLine = "1")
    List<AdminMenu> getProjectAuth(@Param("projectId") Long projectId,@Param("userId") Long userId);
}
