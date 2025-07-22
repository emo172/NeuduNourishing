package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.CustomerNurseItemDTO;
import com.neusoft.yyzx.pojo.Customernurseitem;
import com.neusoft.yyzx.service.CustomernurseitemService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CustomerNurseItemVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/customernurseitem")
@CrossOrigin
@Api(tags = "服务关注管理")
public class CustomernurseitemController {

    @Resource
    private CustomernurseitemService customernurseitemService;

    @ApiOperation("为顾客单个/批量添加护理项目")
    @PostMapping("/addItemToCustomer/{customerId}")
    public ResultVo addItemToCustomer(@PathVariable Integer customerId, @RequestBody List<Customernurseitem> customernurseitems) throws Exception {
        return customernurseitemService.addItemToCustomer(customerId, customernurseitems);
    }

    @ApiOperation("移除客户护理级别级联移除用户当前级别的护理项目")
    @GetMapping("/removeCustomerLevelAndItem")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "levelId", value = "护理级别编号", required = true),
            @ApiImplicitParam(dataType = "int", name = "customerId", value = "用户编号", required = true)
    })
    public ResultVo removeCustomerLevelAndItem(Integer levelId, Integer customerId) throws Exception {
        return customernurseitemService.removeCustomerLevelAndItem(levelId, customerId);
    }

    @ApiOperation("查询某个顾客的护理项目列表-分页")
    @GetMapping("/listCustomerItem")
    public ResultVo<Page<CustomerNurseItemVo>> listCustomerItem(CustomerNurseItemDTO customerNurseItemDTO) throws Exception {
        return customernurseitemService.listCustomerItem(customerNurseItemDTO);
    }

    @ApiOperation("客户续费")
    @PostMapping("/updateNurseItem")
    public ResultVo updateNurseItem(Customernurseitem Customernurseitem) throws Exception {
        customernurseitemService.updateById(Customernurseitem);
        return ResultVo.ok("续费成功");
    }

    @ApiOperation("判断用户是否已经配置了某个指定项目")
    @GetMapping("/isIncludesItemCustomer")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "customerId", value = "用户编号", required = true),
            @ApiImplicitParam(dataType = "int", name = "itemId", value = "护理项目编号", required = true)
    })
    public ResultVo isIncludesItemCustomer(Integer customerId, Integer itemId) throws Exception {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("item_id", itemId);
        qw.eq("customer_id", customerId);
        qw.eq("is_deleted", 0);
        int row = customernurseitemService.count(qw);
        if (row > 0) {
            return ResultVo.fail("当前用户已存在该项目");
        }
        return ResultVo.ok("");

    }

    @ApiOperation("移除客户护理项目")
    @GetMapping("/removeCustomerItem")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "id", value = "主键key", required = true),
    })
    public ResultVo removeCustomerItem(Integer id) throws Exception {
        Customernurseitem customernurseitem = new Customernurseitem();
        customernurseitem.setIsDeleted(1);
        customernurseitem.setId(id);
        customernurseitemService.updateById(customernurseitem);
        return ResultVo.ok("移除成功");
    }
}
