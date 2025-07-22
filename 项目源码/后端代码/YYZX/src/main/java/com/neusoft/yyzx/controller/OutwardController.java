package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.neusoft.yyzx.dto.OutwardDTO;
import com.neusoft.yyzx.pojo.Outward;
import com.neusoft.yyzx.service.OutwardService;
import com.neusoft.yyzx.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outward")
@CrossOrigin
@Api(tags = "外出管理")
public class OutwardController {

    @Autowired
    private OutwardService outwardService;

    @ApiOperation("查询外出记录")
    @PostMapping("/queryOutwardVo")
    public ResultVo queryOutwardVo(@ModelAttribute OutwardDTO outwardDTO) throws Exception {
        return outwardService.queryOutwardVo(outwardDTO);
    }

    @ApiOperation("添加外出审批")
    @PostMapping("/addOutward")
    public ResultVo addOutward(@RequestBody Outward outward) throws Exception {
        // 设置预期返回时间
        if (outward.getExpectedreturntime() == null) {
            // 使用 java.util.Date 类型，而不是 LocalDateTime
            java.util.Date tomorrow = new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            outward.setExpectedreturntime(tomorrow);
        }
        outwardService.save(outward);
        return ResultVo.ok("添加成功");
    }

    @ApiOperation("审批外出申请")
    @PostMapping("/examineOutward")
    public ResultVo examineOutward(Outward outward) throws Exception {
        return outwardService.examineOutward(outward);
    }

    @ApiOperation("撒回外出申请")
    @PostMapping("/delOutward")
    public ResultVo delOutward(Integer id) throws Exception {
        UpdateWrapper<Outward> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_deleted", 1);
        outwardService.update(updateWrapper);
        return ResultVo.ok("撒回成功");
    }

    @ApiOperation("登记回院时间")
    @PostMapping("/updateBackTime")
    public ResultVo updateBackTime(Outward outward) throws Exception {
        UpdateWrapper<Outward> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", outward.getId());
        updateWrapper.set("actual_return_time", outward.getActualreturntime());
        outwardService.update(updateWrapper);
        return ResultVo.ok("登记成功");
    }
}