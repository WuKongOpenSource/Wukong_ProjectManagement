package com.kakarote.work.common.project;

import com.kakarote.work.entity.BO.ProjectBoardBO;
import lombok.Data;

import java.util.List;

@Data
public class ResetBoardBO {
    private List<ProjectBoardBO> boardBOS;
    private  Long schemeRelationId;
    private Long projectId;
    private Integer taskType;
}
