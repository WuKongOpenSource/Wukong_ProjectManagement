package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.ResetBoardBO;
import com.kakarote.work.entity.PO.ProjectEventStatus;
import com.kakarote.work.entity.PO.ProjectSchemeRelationBoard;
import com.kakarote.work.entity.VO.ProjectBoardVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 项目配置方案和事件关系表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
public interface IProjectSchemeRelationBoardService extends BaseService<ProjectSchemeRelationBoard> {
    /**
     * @Description: 查看看板列表
     * @Author: guole
     * @Company: 郑州卡卡罗特软件科技有限公司
     * @Date: 2022/9/26 9:31
     */
       List<ProjectBoardVO> queryBoardList(Long schemeRelationId,Long projectId) ;
    /**
     * 功能描述: 重置看板
     * 〈〉
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/23 11:36
     */
    void  resetBoard(@RequestBody ResetBoardBO resetBoardBO);

    /**
     * 功能描述: 未添加的状态
     * 〈〉
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/27 17:59
     */
    List<ProjectEventStatus>  notAddStatus(Long schemeRelationId,Long projectId);

    void deleteByStatusCountEmpty();

}
