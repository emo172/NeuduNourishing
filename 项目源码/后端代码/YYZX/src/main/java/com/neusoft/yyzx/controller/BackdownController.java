package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.neusoft.yyzx.dto.BackdownDTO;
import com.neusoft.yyzx.pojo.Backdown;
import com.neusoft.yyzx.pojo.Bed;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.service.BackdownService;
import com.neusoft.yyzx.service.BedService;
import com.neusoft.yyzx.service.CustomerService;
import com.neusoft.yyzx.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backdown")
@CrossOrigin
@Api(tags = "退住管理")
public class BackdownController {

    @Autowired
    private BackdownService backdownService;

    @Autowired
    private BedService bedService;

    @Autowired
    private CustomerService customerService;

    @ApiOperation("查询退住信息")
    @PostMapping("/listBackdown")
    public ResultVo listBackdown(BackdownDTO backdownDTO) throws Exception {
        return backdownService.listBackdownVo(backdownDTO);
    }

    @ApiOperation("添加退住审批")
    @PostMapping("/addBackdown")
    public ResultVo addBackdown(Backdown backdown) throws Exception {
        backdownService.save(backdown);
        return ResultVo.ok("添加成功");
    }

    @ApiOperation("审批退住")
    @PostMapping("/examineBackdown")
    public ResultVo examineBackdown(Backdown backdown) throws Exception {
        Backdown bd = backdownService.getById(backdown.getId());
        // 审批通过
        if (backdown.getAuditstatus() == 1) {
            // 修改床铺记录，对应床铺状态改为空闲
            Customer cs = customerService.getById(bd.getCustomerId());
            Bed bed = new Bed();
            bed.setId(cs.getBedId());
            bed.setBedStatus(1);
            bedService.updateById(bed);
        }
        return backdownService.examineBackdown(backdown);
    }

    @ApiOperation("撒回退住申请")
    @PostMapping("/delBackdown")
    public ResultVo delBackdown(Integer id) throws Exception {
        UpdateWrapper<Backdown> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_deleted", 1);
        backdownService.update(updateWrapper);
        return ResultVo.ok("撒回成功");
    }
}
