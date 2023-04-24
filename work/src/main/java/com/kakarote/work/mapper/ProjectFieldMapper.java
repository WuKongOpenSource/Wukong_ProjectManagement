package com.kakarote.work.mapper;

import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.ProjectField;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 自定义字段表 Mapper 接口
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
public interface ProjectFieldMapper extends BaseMapper<ProjectField> {

    /**
     * 删除字段数据
     *
     * @param fieldId fieldId
     * @param table   table
     */
    void deleteFieldDataByIds(@Param("fieldId") Long fieldId, @Param("table") String table);

    /**
     * 删除字段数据
     *
     * @param fieldId fieldId
     */
    void deleteDataByIds(@Param("fieldId") Long fieldId);
}
