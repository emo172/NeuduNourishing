package com.neusoft.yyzx.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "NurseRecordsVo对象", description = "")
public class NurseRecordsVo {
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "逻辑删除标记（0：显示；1：隐藏）")
    private Integer isDeleted;

    @ApiModelProperty(value = "客户ID")
    private Integer customerId;

    @ApiModelProperty(value = "护理项目ID")
    private Integer itemId;

    @ApiModelProperty(value = "护理时间")
    private Date nursingTime;

    @ApiModelProperty(value = "护理内容")
    private String nursingContent;

    @ApiModelProperty(value = "护理数量")
    private Integer nursingCount;

    @ApiModelProperty(value = "护理人员ID")
    private Integer userId;

    @ApiModelProperty(value = "护理人姓名")
    private String nickname;

    @ApiModelProperty(value = "护理人电话号码")
    private String phoneNumber;

    @ApiModelProperty(value = "护理项目编号")
    private String serialNumber;

    @ApiModelProperty(value = "护理项目名称")
    private String nursingName;
}
