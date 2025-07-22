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
@ApiModel(value = "OutRecordVo对象", description = "")
public class OutwardVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "逻辑删除标记（θ:显示； 1：隐藏）")
    private Integer isDeleted;

    @ApiModelProperty(value = "客户ID")
    private Integer customerId;

    @ApiModelProperty(value = "外出事由")
    private String outgoingReason;

    @ApiModelProperty(value = "外出时间")
    private Date outgoingTime;

    @ApiModelProperty(value = "预计回院时间")
    private Date expectedreTurntime;

    @ApiModelProperty(value = "实际回院时间")
    private Date actualreTurntime;

    @ApiModelProperty(value = "陪同人")
    private String escorted;

    @ApiModelProperty(value = "与老人关系")
    private String relation;

    @ApiModelProperty(value = "陪同人电话")
    private String escortedTel;

    @ApiModelProperty(value = "审批状态 0-已提交 1-同意 2-拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "审批人")
    private String auditPerson;

    @ApiModelProperty(value = "审批时间")
    private Date auditTime;

    @ApiModelProperty(value = "护理人姓名")
    private String nickname;

    @ApiModelProperty(value = "护理人电话号码")
    private String phoneNumber;

    @ApiModelProperty(value = "护理项目编号")
    private String serialNumber;

    @ApiModelProperty(value = "护理项目名称")
    private String nursingName;

    @ApiModelProperty(value = "客户名称")
    private String customerName;
}
