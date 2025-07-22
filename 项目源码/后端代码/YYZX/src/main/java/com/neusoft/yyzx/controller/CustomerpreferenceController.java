package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.CustomerPreferenceDTO;
import com.neusoft.yyzx.pojo.Customerpreference;
import com.neusoft.yyzx.service.CustomerpreferenceService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CustomerPreferenceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/customerpreference")
@CrossOrigin
@Api(tags = "客户喜好管理")
public class CustomerpreferenceController {

    @Resource
    private CustomerpreferenceService customerpreferenceService;

    @PostMapping("/addCustomerpreference")
    @ApiOperation("为顾客单个添加喜好")
    public ResultVo addCustomerpreference(@RequestBody Customerpreference customerpreference) throws Exception {
        customerpreferenceService.save(customerpreference);
        return ResultVo.ok("添加顾客喜好");
    }

    @ApiOperation("更新顾客喜好")
    @PostMapping("/updateCustomerpreference")
    public ResultVo updateCustomerpreference(@RequestBody Customerpreference customerpreference) throws Exception {
        customerpreferenceService.updateById(customerpreference);
        return ResultVo.ok("更新顾客喜好");
    }

    @ApiOperation("删除顾客喜好")
    @GetMapping("/removeCustomerpreference")
    public ResultVo removeCustomerpreference(Integer id) throws Exception {
        customerpreferenceService.removeById(id);
        return ResultVo.ok("删除顾客喜好");
    }

    @GetMapping("/listCustomerpreferencePage")
    @ApiOperation("顾客喜好查询（分页）/可以根据顾客姓名查询")
    public ResultVo<Page<CustomerPreferenceVo>> listCustomerpreferencePage(CustomerPreferenceDTO customerPreferenceDTO) throws Exception {
        return customerpreferenceService.ListCustomerpreferenceVoPage(customerPreferenceDTO);
    }
}
