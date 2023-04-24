package com.kakarote.work.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.common.project.BatchSetTaskBO;
import com.kakarote.work.common.project.ProjectTaskUserSortBO;
import com.kakarote.work.common.project.ProjectUserTaskQueryBO;
import com.kakarote.work.entity.BO.*;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.VO.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
public interface IProjectTaskService extends BaseService<ProjectTask> {

    public void saveProjectTask(ProjectTask projectTask);

    public ProjectTask queryProjectTaskById(Long projectTaskId);

    public Boolean updateProjectTask(ProjectTask projectTask);

    public BasePage<ProjectTask> queryProjectTaskList(ProjectTaskQueryBO projectTaskQueryBO);

    public ProjectTaskCountVO getProjectByTime(ProjectTaskCountBO projectTaskQueryBO);

    public ProjectTaskCountVO getTaskByTime(ProjectTaskCountBO projectTaskQueryBO);

    public List<ProjectTaskBurnoutVO> getTaskBurnout(ProjectTaskCountBO projectTaskQueryBO);

    public ProjectTaskEventCountVO getProjectTaskEvent(ProjectTaskCountBO projectTaskCountBO);

    public BasePage<ProjectTask> getAllMatters(@RequestBody ProjectTaskQueryBO projectTaskQueryBO);

    public BasePage<ProjectTask> getAllMattersByTaskId(@RequestBody ProjectTaskQueryBO projectTaskQueryBO);

    /**
     * 功能描述: 待规划列表
     * 〈〉
     *
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/28 20:33
     */
    public BasePage<ProjectTask> queryProjectPlanTaskList(ProjectTaskQueryBO projectTaskQueryBO);

    /**
     * 功能描述: 迭代列表
     * 〈〉
     *
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/28 20:33
     */
    public BasePage<ProjectTask> queryProjectIterationTaskList(ProjectTaskQueryBO projectTaskQueryBO);

    /**
     * 功能描述: 待规划列表
     * 〈〉
     *
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/28 20:33
     */
    public BasePage<ProjectTask> queryProjectTaskChildList(ProjectTaskQueryBO projectTaskQueryBO);

    void relevancyChildTask(RelevancyChildTaskBO relevancyChildTaskBO);
    ProjectTask getProjectTaskDetails(Long taskId);
    void relevancyBelongIteration(RelevancyBelongIterationBO relevancyBelongIterationBO);
    void relevancyRelatedDemand(RelevancyRelatedDemandIdBO relatedDemandIdBO);
    JSONObject excelImport(MultipartFile file, Long projectId, Integer taskType) throws IOException;
    public void downloadExcel(HttpServletResponse response, Integer taskType) ;
    public void projectTaskExport(  ProjectTaskExportBO taskExportBO, HttpServletResponse response) ;
    List<JSONObject> projectTaskExportColumn(Integer taskType);

    /**
     * 删除任务
     */
    void deleteTask(Long taskId);

    /**
     * 迭代,需求,任务下的看板任务列表
     */
    List<ProjectBoardVO> queryProjectTaskChildBoardList(ProjectTaskQueryBO projectTaskQueryBO);

    /**
     * 对待办事项进行排序
     */
    void sortBackLog(ProjectTaskUserSortBO pojectTaskUserSortBO);
    /**
     * 功能描述: 查询当前用户的任务列表
     * 〈〉
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2023/2/25 15:53
     */
    public BasePage<ProjectTask> queryUserTaskList(ProjectUserTaskQueryBO userTaskQueryBO);
    Boolean setProgress( ProjectTask projectTask);
    Boolean setPriority(  ProjectTask projectTask);
    void setProjectTaskMainUser(@RequestBody ProjectTask projectTask);
    void batchSetProjectTask(BatchSetTaskBO batchSetTaskBO);
    void  updateProjectTaskTime( ProjectTask projectTask);

    /**
     * 功能描述: <br>
     * 〈查询工作台中各类型数量〉
     * @param userTaskQueryBO
     * @author zyh
     */
    ProjectUserTaskCountVO queryUserTaskCount(ProjectUserTaskQueryBO userTaskQueryBO);

    void projectTaskSetName(ProjectTaskNameBO projectTaskNameBO);
}
