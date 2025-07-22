package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neusoft.yyzx.pojo.Bed;
import com.neusoft.yyzx.service.BedService;
import com.neusoft.yyzx.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/bed")
@Api(tags = "床位管理")
@CrossOrigin
public class BedController {

    @Resource
    private BedService bedService;

    @GetMapping("/findBed")
    @ApiOperation("查询床位信息")
    public ResultVo<List<Bed>> findBed(Bed bed) {
        QueryWrapper qw = new QueryWrapper();
        if (bed.getRoomNo() != null) {
            qw.eq("room_no", bed.getRoomNo());
        }
        if (bed.getBedStatus() != null) {
            qw.eq("bed_status", bed.getBedStatus());
        }
        return ResultVo.ok(bedService.list(qw));
    }
}
