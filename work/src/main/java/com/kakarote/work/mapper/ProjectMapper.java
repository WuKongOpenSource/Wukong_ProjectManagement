package com.kakarote.work.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.common.project.ProjectCountBO;
import com.kakarote.work.entity.BO.ProjectOwnerRoleSimpleBO;
import com.kakarote.work.entity.PO.Project;
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
public interface ProjectMapper extends BaseMapper<Project> {

    public String queryRoleName(@Param("roleId") Long roleId);

    public List<ProjectOwnerRoleSimpleBO> queryOwnerRoleList(@Param("projectId") Long projectId);

    /**
     * 功能描述: <br>
     * 〈根据项目ids查询项目统计项〉
     * @param projectIds
     * @return java.util.List<com.kakarote.work.entity.BO.ProjectCountBO>
     * @author zyh
     */
    List<ProjectCountBO> getProjectCount(@Param("projectIds") List<Long> projectIds, @Param("userId") Long userId);
    @InterceptorIgnore(tenantLine = "1")
    List<Project> allProject();
}
