package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class ProjectUserTaskCountVO {

    @ApiModelProperty(value = "完成数量")
    private Integer all;

    @ApiModelProperty(value = "完成数量")
    private Integer need;

    @ApiModelProperty(value = "完成数量")
    private Integer task;

    @ApiModelProperty(value = "缺陷")
    private Integer defect;

    @ApiModelProperty(value = "迭代")
    private Long iteration;

}
