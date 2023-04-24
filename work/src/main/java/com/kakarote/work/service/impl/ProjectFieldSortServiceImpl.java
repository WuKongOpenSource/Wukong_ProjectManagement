package com.kakarote.work.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.common.project.ProjectFieldSortAddBO;
import com.kakarote.work.common.project.ProjectFieldSortBO;
import com.kakarote.work.common.project.ProjectFieldSortQueryBO;
import com.kakarote.work.constant.ModuleTypeEnum;
import com.kakarote.work.entity.PO.ProjectFieldSort;
import com.kakarote.work.mapper.ProjectFieldSortMapper;
import com.kakarote.work.service.IProjectFieldSortService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字段排序表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
@Service
public class ProjectFieldSortServiceImpl extends BaseServiceImpl<ProjectFieldSortMapper, ProjectFieldSort> implements IProjectFieldSortService {

    /**
     * 功能描述: <br>
     * 〈查询字自定义字段段列表〉
     * @param  projectFieldSortQueryBO
     * @return java.util.List<com.kakarote.work.entity.PO.ProjectFieldSort>
     * @author zyh
     */
    @Override
    public List<ProjectFieldSort> selectUserSort(ProjectFieldSortQueryBO projectFieldSortQueryBO) {
        //查询个人排序
        List<ProjectFieldSort> projectFieldSorts = this.lambdaQuery().eq(ProjectFieldSort::getProjectId, projectFieldSortQueryBO.getProjectId())
                .eq(ProjectFieldSort::getModuleType, projectFieldSortQueryBO.getModuleType())
                .eq(ProjectFieldSort::getUserId, UserUtil.getUserId())
                .list();
        if(CollectionUtil.isEmpty(projectFieldSorts)){
            return getDefaultSort(projectFieldSortQueryBO.getModuleType());
        }
        return projectFieldSorts;
    }

    /**
     * 功能描述: <br>
     * 〈设置个人字段排序〉
     * @param projectFieldSortAddBO
     * @return java.lang.Boolean
     * @throws
     * @author zyh
     * @date: 2023/2/25 11:32
     */
    @Override
    public Boolean addOrUpdateUserSort(ProjectFieldSortAddBO projectFieldSortAddBO) {
        if(ObjectUtil.isEmpty(projectFieldSortAddBO.getProjectId())
                ||ObjectUtil.isEmpty(projectFieldSortAddBO.getModuleType())
                ||CollectionUtil.isEmpty(projectFieldSortAddBO.getFieldSorts())){
            return Boolean.TRUE;
        }
        List<ProjectFieldSortBO> fieldSorts = projectFieldSortAddBO.getFieldSorts();
        //删除旧的
        List<String> fieldNames = fieldSorts.stream().map(ProjectFieldSortBO::getFieldName).collect(Collectors.toList());
        LambdaUpdateWrapper<ProjectFieldSort> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectFieldSort::getProjectId,projectFieldSortAddBO.getProjectId());
        updateWrapper.eq(ProjectFieldSort::getModuleType,projectFieldSortAddBO.getModuleType());
        updateWrapper.eq(ProjectFieldSort::getUserId,UserUtil.getUserId());
        updateWrapper.in(ProjectFieldSort::getFieldName,fieldNames);
        this.remove(updateWrapper);
        //增加新的
        List<ProjectFieldSort> projectFieldSorts = new ArrayList<>();
        for (ProjectFieldSortBO fieldSort : fieldSorts) {
            ProjectFieldSort projectFieldSort = new ProjectFieldSort();
            projectFieldSort.setFieldName(fieldSort.getFieldName());
            projectFieldSort.setName(fieldSort.getName());
            projectFieldSort.setWidth(fieldSort.getWidth());
            projectFieldSort.setProjectId(projectFieldSortAddBO.getProjectId());
            projectFieldSort.setSchemeId(projectFieldSortAddBO.getSchemeId());
            projectFieldSort.setEventId(Long.parseLong(StrUtil.toString(projectFieldSortAddBO.getModuleType())));
            projectFieldSort.setModuleType(projectFieldSortAddBO.getModuleType());
            projectFieldSort.setUserId(UserUtil.getUserId());
            projectFieldSort.setIsHide(fieldSort.getIsHide());
            projectFieldSort.setIsDefault(0);
            projectFieldSorts.add(projectFieldSort);
        }
        this.saveBatch(projectFieldSorts);
        return Boolean.TRUE;
    }

    public List<ProjectFieldSort> getDefaultSort(Integer moduleType) {

        List<ProjectFieldSort> projectFieldSorts = new ArrayList<>();
        if(moduleType.equals(ModuleTypeEnum.ITERATION.getType())){
            projectFieldSorts.add(createProjectFieldSort("num","引用ID",70));
            projectFieldSorts.add(createProjectFieldSort("name","迭代名称",220));
            projectFieldSorts.add(createProjectFieldSort("startTime","开始时间",160));
            projectFieldSorts.add(createProjectFieldSort("stopTime","结束时间",160));
            projectFieldSorts.add(createProjectFieldSort("status","状态",100));
            projectFieldSorts.add(createProjectFieldSort("belongIterationProgress","进度",100));
            projectFieldSorts.add(createProjectFieldSort("mainUserName","负责人",100));
            projectFieldSorts.add(createProjectFieldSort("belongProjectName","所属项目",320));
        }else{
            projectFieldSorts.add(createProjectFieldSort("num","ID",60));
            projectFieldSorts.add(createProjectFieldSort("name","标题",440));
            projectFieldSorts.add(createProjectFieldSort("priority","优先级",80));
            projectFieldSorts.add(createProjectFieldSort("status","状态",100));
            projectFieldSorts.add(createProjectFieldSort("mainUserName","处理人",100));
            projectFieldSorts.add(createProjectFieldSort("createUserName","创建人",100));
            projectFieldSorts.add(createProjectFieldSort("belongProjectName","所属项目",320));
            projectFieldSorts.add(createProjectFieldSort("belongIterationName","所属迭代",160));
            projectFieldSorts.add(createProjectFieldSort("stopTime","截止日期",130));
            projectFieldSorts.add(createProjectFieldSort("createTime","创建日期",160));
        }

        return projectFieldSorts;
    }

    private ProjectFieldSort createProjectFieldSort(String fieldName,String name,Integer width){
        ProjectFieldSort projectFieldSort = new ProjectFieldSort();
        projectFieldSort.setFieldName(fieldName);
        projectFieldSort.setName(name);
        projectFieldSort.setWidth(width);
        projectFieldSort.setIsHide(0);
        return projectFieldSort;
    }
}
