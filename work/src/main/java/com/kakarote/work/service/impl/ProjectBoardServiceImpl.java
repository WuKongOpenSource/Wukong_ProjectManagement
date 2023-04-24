package com.kakarote.work.service.impl;

import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.work.entity.PO.ProjectBoard;
import com.kakarote.work.entity.PO.ProjectEvent;
import com.kakarote.work.mapper.ProjectBoardMapper;
import com.kakarote.work.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 项目管理 看板信息 服务实现类
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Service
public class ProjectBoardServiceImpl extends BaseServiceImpl<ProjectBoardMapper, ProjectBoard> implements IProjectBoardService {
    @Autowired
    private IProjectBoardStatusService projectBoardStatusService;
    @Autowired
    IProjectSchemeRelationService schemeRelationService;
    IProjectSchemeRelationBoardService schemeRelationBoardService;
    @Autowired
    IProjectEventService projectEventService;

    public void add(ProjectBoard projectBoard) {
        Long sorting = this.lambdaQuery().count() + 1;
        projectBoard.setSorting(sorting);
        this.baseMapper.insert(projectBoard);
    }

    @Override
    public void updateSorting(List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            this.lambdaUpdate().eq(ProjectBoard::getProjectBoardId, ids.get(i)).set(ProjectBoard::getSorting, i + 1);
        }
    }






    @Override
    public void initialize() {
        List<ProjectEvent> eventList = projectEventService.list();
        List<ProjectBoard> projectBoardList = new ArrayList<>();
        eventList.forEach(projectEvent -> {
            ProjectBoard projectBoard1 = new ProjectBoard();
            projectBoard1.setBoardName("未开始");
            projectBoard1.setEventId(projectEvent.getId());
            projectBoard1.setSorting(0L);
            projectBoardList.add(projectBoard1);
            ProjectBoard projectBoard2 = new ProjectBoard();
            projectBoard2.setBoardName("进行中");
            projectBoard2.setEventId(projectEvent.getId());
            projectBoard2.setSorting(1L);
            projectBoardList.add(projectBoard2);
            ProjectBoard projectBoard3 = new ProjectBoard();
            projectBoard3.setBoardName("已完成");
            projectBoard3.setEventId(projectEvent.getId());
            projectBoard3.setSorting(2L);
            projectBoardList.add(projectBoard3);
        });

        this.saveBatch(projectBoardList);
    }
}
