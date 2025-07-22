package com.neusoft.yyzx.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Beddetails对象", description = "Beddetails实体类")
public class Beddetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "逻辑删除标记（0:显示;1：隐藏）")
    private Integer isDeleted;

    @ApiModelProperty(value = "床位起始日期")
    private Date startDate;

    @ApiModelProperty(value = "床位结束日期")
    private Date endDate;

    @ApiModelProperty(value = "床位详情信息")
    private String bedDetails;

    @ApiModelProperty(value = "顾客ID")
    private Integer customerId;

    @ApiModelProperty(value = "床位ID")
    private Integer bedId;
}
