package com.neusoft.yyzx.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CustomerNurseItemDTO-顾客护理项目查询条件", description = "CustomerNurseItemDTO")
public class CustomerNurseItemDTO {
    @ApiModelProperty(value = "客户编号")
    private Integer customerId;

    @ApiModelProperty(value = "页码")
    private Integer pageSize;
}
