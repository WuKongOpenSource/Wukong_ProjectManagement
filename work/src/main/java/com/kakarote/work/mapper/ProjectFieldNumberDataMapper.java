package com.kakarote.work.mapper;

import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.ProjectFieldNumberData;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 自定义编号字段存值表 Mapper 接口
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
public interface ProjectFieldNumberDataMapper extends BaseMapper<ProjectFieldNumberData> {
    /**
     * 查询最大数
     *
     * @param fieldId   fieldId
     * @param startDate startDate
     * @param endDate   endDate
     * @return Integer
     */
    Integer queryMaxNumber(@Param("fieldId") Long fieldId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 新增自定义编号字段类型的数据
     *
     * @param dataMap data
     */
    void saveBatchData(Map<String, Object> dataMap);

    /**
     * 更新
     *
     * @param dataMap dataMap
     */
    void updateBatchData(Map<String, Object> dataMap);

    /**
     * 查询
     *
     * @param dataMap dataMap
     * @return Integer
     */
    Integer selectBatchData(Map<String, Object> dataMap);
}
