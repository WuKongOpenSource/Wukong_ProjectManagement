package com.kakarote.work.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 事项完成情况统计VO
 * </p>
 *
 * @author zhangzhiwei
 * @since 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProjectTaskNumVO对象", description = "事项完成情况统计VO")
public class ProjectTaskNumVO {

    @ApiModelProperty(value = "完成数量")
    private Integer num;

    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;


}
