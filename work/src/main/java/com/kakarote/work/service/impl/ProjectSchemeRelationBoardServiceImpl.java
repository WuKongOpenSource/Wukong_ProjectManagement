package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.work.entity.BO.ProjectBoardBO;
import com.kakarote.work.entity.BO.ResetBoardBO;
import com.kakarote.work.entity.PO.ProjectBoardStatus;
import com.kakarote.work.entity.PO.ProjectEventStatus;
import com.kakarote.work.entity.PO.ProjectSchemeRelation;
import com.kakarote.work.entity.PO.ProjectSchemeRelationBoard;
import com.kakarote.work.entity.VO.ProjectBoardVO;
import com.kakarote.work.mapper.ProjectSchemeRelationBoardMapper;
import com.kakarote.work.service.IProjectBoardStatusService;
import com.kakarote.work.service.IProjectEventStatusService;
import com.kakarote.work.service.IProjectSchemeRelationBoardService;
import com.kakarote.work.service.IProjectSchemeRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectSchemeRelationBoardServiceImpl extends BaseServiceImpl<ProjectSchemeRelationBoardMapper, ProjectSchemeRelationBoard> implements IProjectSchemeRelationBoardService {
    @Autowired
    private IProjectBoardStatusService projectBoardStatusService;
    @Autowired
    IProjectSchemeRelationService schemeRelationService;
    @Autowired
    IProjectEventStatusService eventStatusService;

    @Override
    public List<ProjectBoardVO> queryBoardList(Long schemeRelationId, Long projectId) {
        List<ProjectBoardVO> boardVOS = new ArrayList<>();
        ProjectSchemeRelation psr = schemeRelationService.getById(schemeRelationId);

        List<ProjectSchemeRelationBoard> boardRelationList = this.lambdaQuery().eq(ProjectSchemeRelationBoard::getSchemeRelationId, schemeRelationId).
                eq(ProjectSchemeRelationBoard::getProjectId, projectId
                ).list();

        for (ProjectSchemeRelationBoard board : boardRelationList) {
            ProjectBoardVO projectBoardVO = BeanUtil.toBean(board, ProjectBoardVO.class);
            projectBoardVO.setProjectBoardId(board.getId());
            projectBoardVO.setEventId(psr.getEventId());
            projectBoardVO.setStatusList(this.projectBoardStatusService.queryBoardStatusByBoardId(board.getId()));
            boardVOS.add(projectBoardVO);
        }

        return boardVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetBoard(ResetBoardBO resetBoardBO) {
//        for (ProjectBoardBO boardBO : resetBoardBO.getBoardBOS()) {
//            if(boardBO.boardName.equals("未开始")||boardBO.boardName.equals("进行中")||boardBO.boardName.equals("已完成")){
//                throw new CrmException(SystemCodeEnum.BOARD_ERROR);
//
//
//            }
//        }
        //清除旧的数据
        this.lambdaUpdate().eq(ProjectSchemeRelationBoard::getSchemeRelationId, resetBoardBO.getSchemeRelationId()).
                eq(ProjectSchemeRelationBoard::getProjectId, resetBoardBO.getProjectId()).remove();
        //新增新的数据
        for (ProjectBoardBO boardBO : resetBoardBO.getBoardBOS()) {
            ProjectSchemeRelationBoard schemeRelationBoard = BeanUtil.toBean(boardBO, ProjectSchemeRelationBoard.class);
            schemeRelationBoard.setSchemeRelationId(resetBoardBO.getSchemeRelationId());
            schemeRelationBoard.setProjectId(resetBoardBO.getProjectId());
            this.save(schemeRelationBoard);
            List<ProjectBoardStatus> projectBoardStatusList = new ArrayList<>();
            boardBO.getBoardStatusBOList().forEach(projectBoardStatusBO -> {
                ProjectBoardStatus pbs = BeanUtil.toBean(projectBoardStatusBO, ProjectBoardStatus.class);
                pbs.setBoardId(schemeRelationBoard.getId());
                projectBoardStatusList.add(pbs);
            });
            projectBoardStatusService.saveBatch(projectBoardStatusList);
        }

    }

    @Override
    public List<ProjectEventStatus> notAddStatus(Long schemeRelationId, Long projectId) {
        ProjectSchemeRelation psr = schemeRelationService.getById(schemeRelationId);
        List<ProjectEventStatus> eventStatuts = eventStatusService.notAddStatus(psr.getEventId(), psr.getId(), projectId);
        return eventStatuts;

    }

    @Override
    public void deleteByStatusCountEmpty() {
        this.baseMapper.deleteByStatusCountEmpty();
    }
}
