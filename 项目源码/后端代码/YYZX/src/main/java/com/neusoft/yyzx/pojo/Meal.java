package com.neusoft.yyzx.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Meal对象", description = "Meal实体类")
public class Meal implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "星期几")
    private String weekDay;

    @ApiModelProperty(value = "食品ID")
    private Integer foodId;

    @ApiModelProperty(value = "食品类型（1.早餐、2.午餐、3.晚餐）")
    private Integer mealType;

    @ApiModelProperty(value = "口味（多糖、多盐、少糖、少盐）")
    private String taste;

    @ApiModelProperty(value = "逻辑删除标记（0：显示；1：隐藏）")
    private Integer isDeleted;
}
