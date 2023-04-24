package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectFieldConfig;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
public interface IProjectFieldConfigService extends BaseService<ProjectFieldConfig> {

    /**
     * 获取下个字段名称
     *
     * @param existNameList 已存在的标签
     * @param depth         职位
     * @param isCreateField 是否创建
     * @return fieldName
     */
    String getNextFieldName(Integer fieldType, List<String> existNameList, Integer depth, boolean isCreateField);
}
