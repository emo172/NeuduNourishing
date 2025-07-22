package com.neusoft.yyzx.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "KhxxDTO-客户信息查询条件", description = "KhxxDTO")
public class KhxxDTO {
    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

    @ApiModelProperty(value = "老人类型 1-自理老人 2-护理老人 3-无管家")
    private Integer manType;//老人类型 1-自理老人 2-护理老人 3-无管家

    @ApiModelProperty(value = "系统健康管家（护工)")
    private Integer userId;

}