package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.pojo.Nursecontent;
import com.neusoft.yyzx.pojo.Nurselevel;
import com.neusoft.yyzx.pojo.Nurselevelitem;
import com.neusoft.yyzx.service.NursecontentService;
import com.neusoft.yyzx.service.NurselevelService;
import com.neusoft.yyzx.service.NurselevelitemService;
import com.neusoft.yyzx.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nurselevel")
@Api(tags = "护理级别管理")
@CrossOrigin
public class NurselevelController {
    @Autowired
    private NurselevelService nurselevelService;

    @Autowired
    private NurselevelitemService nurselevelitemService;

    @Autowired
    private NursecontentService nursecontentService;

    @ApiOperation("添加护理级别")
    @PostMapping("/addNurseLevel")
    public ResultVo addNurseLevel(@RequestBody Nurselevel nurselevel) throws Exception {
        nurselevelService.save(nurselevel);
        return ResultVo.ok("添加护理级别成功");
    }

    @ApiOperation("更新护理级别")
    @PostMapping("/updateNurseLevel")
    public ResultVo updateNurseLevel(@RequestBody Nurselevel nurselevel) throws Exception {
        nurselevelService.updateById(nurselevel);
        return ResultVo.ok("更新护理级别成功");
    }

    @ApiOperation("删除护理级别")
    @GetMapping("/removeNurseLevel")
    public ResultVo removeNurseLevel(Integer id) throws Exception {
        nurselevelService.removeById(id);
        return ResultVo.ok("删除护理级别成功");
    }

    @ApiOperation("查询护理级别列表（分页）")
    @GetMapping("/listNurseLevel")
    public ResultVo listNurseLevel(Nurselevel nurselevel,
                                   @RequestParam(defaultValue = "1") int pageNo,
                                   @RequestParam(defaultValue = "9999") int pageSize) throws Exception {
        QueryWrapper<Nurselevel> qw = new QueryWrapper<>();
        if (nurselevel.getLevelStatus() != null) {
            qw.eq("level_status", nurselevel.getLevelStatus());
        }
        if (nurselevel.getLevelName() != null && !nurselevel.getLevelName().isEmpty()) {
            qw.like("level_name", nurselevel.getLevelName());
        }

        Page<Nurselevel> page = new Page<>(pageNo, pageSize);
        nurselevelService.page(page, qw);

        return ResultVo.ok(page);
    }

    @ApiOperation("根据护理级别查询护理项目-不分页")
    @GetMapping("/listNurseItemByLevel")
    public ResultVo<List<Nursecontent>> listNurseItemByLevel(Integer levelId) throws Exception {
        return ResultVo.ok(nursecontentService.listNurseItemByLevel(levelId));
    }

    @ApiOperation("护理项目的配置")
    @PostMapping("/addItemToLevel")
    public ResultVo addItemToLevel(@RequestBody Nurselevelitem nurselevelitem) throws Exception {
        // 判断当前级别是否存在相同的护理项目
        QueryWrapper<Nurselevelitem> qw = new QueryWrapper<>();
        qw.eq("level_id", nurselevelitem.getLevelId());
        qw.eq("item_id", nurselevelitem.getItemId());
        int row = nurselevelitemService.count(qw);

        if (row > 0) {
            return ResultVo.fail("当前级别已存在相同项目");
        }

        nurselevelitemService.save(nurselevelitem);
        return ResultVo.ok("添加成功");
    }

    @ApiOperation("删除护理级别的护理项目")
    @GetMapping("/removeNurseLevelItem")
    public ResultVo removeNurseLevelItem(Integer levelId, Integer itemId) throws Exception {
        QueryWrapper<Nurselevelitem> qw = new QueryWrapper<>();
        qw.eq("level_id", levelId);
        qw.eq("item_id", itemId);
        nurselevelitemService.remove(qw);
        return ResultVo.ok("删除成功");
    }
}