package com.kakarote.work.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.AdminRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目表 Mapper 接口
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
public interface ProjectRoleMapper extends BaseMapper<AdminRole> {


    /**
     * 查询角色菜单
     *
     * @param parentId parentId
     * @param roleId   roleId
     * @return data
     */
    public List<Long> getRoleMenu(@Param("parentId") Long parentId, @Param("roleId") Long roleId);
}
