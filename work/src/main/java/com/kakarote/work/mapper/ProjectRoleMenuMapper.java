package com.kakarote.work.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.AdminRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色菜单对应关系表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface ProjectRoleMenuMapper extends BaseMapper<AdminRoleMenu> {


    /**
     * 初始化项目管理角色
     *
     * @param dataList 数据列表
     */
    @InterceptorIgnore(tenantLine = "1")
    public void initProjectRole(@Param("dataList") List<Map<String, Object>> dataList);

    /**
     * 初始化项目状态
     *
     * @param dataList 数据列表
     */
    @InterceptorIgnore(tenantLine = "1")
    public void initProjectStatus(@Param("dataList") List<Map<String, Object>> dataList);

    /**
     * 初始化菜单
     * @param dataList 数据列表
     */
    @InterceptorIgnore(tenantLine = "1")
    public void initRoleMenuList(@Param("dataList") List<AdminRoleMenu> dataList);
}
