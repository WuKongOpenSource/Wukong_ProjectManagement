package com.kakarote.work.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.ProjectStatus;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 项目管理：状态表 Mapper 接口
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
public interface ProjectStatusMapper extends BaseMapper<ProjectStatus> {
    @InterceptorIgnore(tenantLine = "1")
    @Select("select  count(1) from wk_project_status")
    Integer queryStatusByCompany();
}
