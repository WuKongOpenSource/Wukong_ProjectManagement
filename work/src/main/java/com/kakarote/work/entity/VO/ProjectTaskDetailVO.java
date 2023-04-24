package com.kakarote.work.entity.VO;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.work.entity.BO.FileEntity;
import com.kakarote.work.entity.BO.SimpleCrmEntity;
import com.kakarote.work.entity.BO.ProjectTaskLabelBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wyq
 */
@Data
@ApiModel("任务详情信息")
public class ProjectTaskDetailVO {
    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "0 带规划 1迭代 2需求 3任务 4缺陷 5工时")
    private Integer type;

    @ApiModelProperty(value = "事件状态")
    private Long status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "优先级 从大到小 3高 2中 1低 0无")
    private Integer priority;

    @ApiModelProperty(value = "子任务")
    private List<ProjectTaskDetailVO> childTask;

    @ApiModelProperty(value = "附件")
    private List<FileEntity> file;

    @ApiModelProperty(value = "客户列表")
    private List<SimpleCrmEntity> customerList = new ArrayList<>();

    @ApiModelProperty(value = "联系人列表")
    private List<SimpleCrmEntity> contactsList = new ArrayList<>();

    @ApiModelProperty(value = "商机列表")
    private List<SimpleCrmEntity> businessList = new ArrayList<>();

    @ApiModelProperty(value = "合同列表")
    private List<SimpleCrmEntity> contractList = new ArrayList<>();

    @ApiModelProperty(value = "回款列表")
    private List<SimpleCrmEntity> receivablesList = new ArrayList<>();

    @ApiModelProperty(value = "标签列表")
    private List<ProjectTaskLabelBO> labelList;

    private JSONObject authList;
}
