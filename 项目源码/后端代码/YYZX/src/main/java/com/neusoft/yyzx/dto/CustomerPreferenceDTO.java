package com.neusoft.yyzx.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CustomerPreferenceDTO-顾客喜好查询条件", description = "CustomerPreferenceDTO")
public class CustomerPreferenceDTO {
    @ApiModelProperty(value = "喜好编号")
    private Integer customerPreferenceId;

    @ApiModelProperty(value = "顾客姓名")
    private String customerName;

    @ApiModelProperty(value = "页码")
    private Integer pageSize;
}