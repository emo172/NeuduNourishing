package com.neusoft.yyzx.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MealDTO - 食谱日历查询条件", description = "MealDTO")
public class MealDTO {
    @ApiModelProperty(value = "食谱编号")
    private Integer mealId;

    @ApiModelProperty(value = "星期")
    private String weekDay;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "每页信息数")
    private Integer pageSize;

    @ApiModelProperty(value = "餐饮类型（早餐/午餐/晚餐）")
    private Integer mealType;
}