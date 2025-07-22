package com.neusoft.yyzx.vo;

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
@ApiModel(value = "BackdownVo对象", description = "")
public class BackdownVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "逻辑删除标记（0：显示；1：隐藏）")
    private Integer isDeleted;

    @ApiModelProperty(value = "客户ID")
    private Integer customerId;

    @ApiModelProperty(value = "退住时间")
    private Date retreatTime;

    @ApiModelProperty(value = "退住类型 0-正常退住 1-死亡退住 2-保留床位")
    private Integer retreatType;

    @ApiModelProperty(value = "退住原因")
    private String retreatReason;

    @ApiModelProperty(value = "审批状态  0-已提交 1-同意  2-拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "审批人")
    private String auditPerson;

    @ApiModelProperty(value = "审批时间")
    private Date auditTime;

    @ApiModelProperty(value = "床位")
    private String bedDetails;

    @ApiModelProperty(value = "入住时间")
    private Date checkinDate;

    @ApiModelProperty(value = "床位")
    private Integer bedId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;
}
