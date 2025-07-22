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
@ApiModel(value = "BedDetailsVo对象", description = "")
public class BedDetailsVo {
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "逻辑删除标记（0：显示；1：隐藏）")
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

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "性别  0：男  1：女")
    private Integer customerSex;

    @ApiModelProperty(value = "房间号")
    private String roomNo;

}
