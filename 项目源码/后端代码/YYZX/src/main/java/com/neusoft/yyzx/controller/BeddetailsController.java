package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.BedDetailsDTO;
import com.neusoft.yyzx.dto.ExchangeDTO;
import com.neusoft.yyzx.pojo.Beddetails;
import com.neusoft.yyzx.service.BeddetailsService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.BedDetailsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/beddetails")
@Api(tags = "床位详情管理")
@CrossOrigin
public class BeddetailsController {
    @Resource
    private BeddetailsService beddetailsService;

    @GetMapping("/listBedDetailsVoPage")
    @ApiOperation("床位详细列表动态查询（分页）")
    public ResultVo<Page<BedDetailsVo>> listBedDetailsVoPage(BedDetailsDTO bedDetailsDTO) throws Exception {
        return beddetailsService.listBedDetailsVoPage(bedDetailsDTO);
    }

    @PostMapping("/updateBedDetails")
    @ApiOperation("更新床位使用详情-只能修改床位使用结束时间")
    public ResultVo updateBedDetails(Beddetails beddetails) throws Exception {
        beddetailsService.updateById(beddetails);
        return ResultVo.ok("编辑成功");
    }

    @PostMapping("/exchangeBed")
    @ApiOperation("床位调换")
    public ResultVo exchangeBed(ExchangeDTO exchangeDTO) throws Exception {
        return beddetailsService.exchangeBed(exchangeDTO);
    }

    @GetMapping("/delBedDetails")
    @ApiOperation("删除记录")
    public ResultVo delBedDetails(Integer id) throws Exception {
        beddetailsService.removeById(id);
        return ResultVo.ok("删除成功");
    }
}

