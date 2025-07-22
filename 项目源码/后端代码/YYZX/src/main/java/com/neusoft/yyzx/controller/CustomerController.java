package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.KhxxDTO;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.service.CustomerService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.KhxxCustomerVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/customer")
@CrossOrigin
@Api(tags = "客户管理")
public class CustomerController {
    @Resource
    private CustomerService customerService;

    @ApiOperation("入住登记")
    @PostMapping("/rzdj")
    public ResultVo addCustomer(Customer customer) throws Exception {
        return customerService.addCustomer(customer);
    }

    @ApiOperation("客户信息动态查询（分页）")
    @GetMapping("/listKhxxPage")
    public ResultVo<Page<KhxxCustomerVo>> listKhxxPage(KhxxDTO khxxDTO) throws Exception {
        return customerService.khxxFindCustomer(khxxDTO);
    }

    @ApiOperation("根据key删除")
    @GetMapping("/remove")
    public ResultVo remove(Integer id, Integer bedId) throws Exception {
        return customerService.removeCustomer(id, bedId);
    }

    @ApiOperation("编辑客户信息")
    @PostMapping("/editKhxx")
    public ResultVo editKhxx(Customer customer) throws Exception {
        return customerService.editCustomer(customer);
    }
}