package com.kakarote.work.mapper;


import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.BO.ProjectTaskFollowsQueryBO;
import com.kakarote.work.entity.PO.ProjectTaskFollows;
import com.kakarote.work.entity.VO.ProjectTaskFollowsVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 项目事项跟进记录/客户动态表 Mapper 接口
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
public interface ProjectTaskFollowsMapper extends BaseMapper<ProjectTaskFollows> {

    BasePage<ProjectTaskFollowsVO> getProjectFollowsPageList(BasePage<ProjectTaskFollowsQueryBO> page, @Param("projectTaskFollowsQueryBO") ProjectTaskFollowsQueryBO projectTaskFollowsQueryBO);

}
