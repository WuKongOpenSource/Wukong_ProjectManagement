package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("事项文件日志记录")
public class ProjectTaskFileLog {

    @ApiModelProperty("事项ID")
    Long taskIds;

    @ApiModelProperty("1 上传文件 2下载文件 3删除文件")
    Integer operationType;

    @ApiModelProperty("1文件名")
    String fileName;
}
