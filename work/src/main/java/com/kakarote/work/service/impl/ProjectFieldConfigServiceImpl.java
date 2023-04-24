package com.kakarote.work.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.work.common.project.SystemCodeEnum;
import com.kakarote.work.entity.PO.ProjectFieldConfig;
import com.kakarote.work.mapper.ProjectFieldConfigMapper;
import com.kakarote.work.service.IProjectFieldConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
@Service
public class ProjectFieldConfigServiceImpl extends BaseServiceImpl<ProjectFieldConfigMapper, ProjectFieldConfig> implements IProjectFieldConfigService {


    /**
     * @param fieldType     字段类型
     * @param existNameList 已存在的标签
     * @param isCreateField 是否创建索引字段
     * @return fieldName
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getNextFieldName(Integer fieldType, List<String> existNameList, Integer depth, boolean isCreateField) {
        if (depth < 0) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
        }
        QueryWrapper<ProjectFieldConfig> configQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ProjectFieldConfig> eq = configQueryWrapper.select("field_name")
                .eq("field_type", parseType(fieldType));
        if (existNameList.size() > 0) {
            eq.notIn("field_name", existNameList);
        }
        List<String> nameList = listObjs(configQueryWrapper, Object::toString);
        if (nameList.size() == 0) {
            String name;
            try {
                name = "field_" + RandomUtil.randomString(RandomUtil.BASE_CHAR, 6);
                ProjectFieldConfig projectFieldConfig = new ProjectFieldConfig();
                projectFieldConfig.setFieldType(parseType(fieldType));
                projectFieldConfig.setFieldName(name);
                save(projectFieldConfig);
                if (isCreateField) {
//                    ElasticUtil.addField(restTemplate.getClient(), jxcFieldConfig, fieldType);
                }
            } catch (Exception e) {
                log.error("保存出现冲突", e);
                name = getNextFieldName(fieldType, existNameList, --depth, isCreateField);
            }
            nameList.add(name);
        }
        return nameList.get(0);
    }

    /**
     * 初始化成elastic数据类型
     *
     * @param fieldType 字段类型
     * @return esType 1 keyword 2 date 3 number 4 nested 5 datetime
     */
    private Integer parseType(Integer fieldType) {
        Integer[] nested = new Integer[]{2, 9};
        Integer[] date = new Integer[]{4, 13};
        Integer[] number = new Integer[]{5, 6};
        int four = 4;
        if (Arrays.asList(nested).contains(fieldType)) {
            return four;
        }
        if (Arrays.asList(date).contains(fieldType)) {

            if (Objects.equals(four, fieldType)) {
                return 2;
            } else {
                return 5;
            }
        }
        if (Arrays.asList(number).contains(fieldType)) {
            return 3;
        }
        return 1;
    }
}
