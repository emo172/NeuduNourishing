package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.NurseItemDTO;
import com.neusoft.yyzx.pojo.Nursecontent;
import com.neusoft.yyzx.service.NursecontentService;
import com.neusoft.yyzx.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nursecontent")
@CrossOrigin
@Api(tags = "护理项目管理")
public class NursecontentController {

    @Autowired
    private NursecontentService nursecontentservice;

    @ApiOperation("添加护理项目")
    @PostMapping("/addNurseItem")
    public ResultVo addNurseItem(@RequestBody Nursecontent nursecontent) throws Exception {
        nursecontent.setIsDeleted(0);
        nursecontentservice.save(nursecontent);
        return ResultVo.ok("添加成功");
    }

    @ApiOperation("查询护理项目-分页")
    @GetMapping("/findNurseItemPage")
    public ResultVo<Page<Nursecontent>> findNurseItemPage(NurseItemDTO nurseItemDTO) throws Exception {
        // 安全处理分页参数
        long current = 1; // 默认第一页
        long size = 10;    // 默认每页6条

        // 检查 DTO 是否为空（防止整个对象为 null）
        if (nurseItemDTO == null) {
            nurseItemDTO = new NurseItemDTO();
        }

        // 如果前端传递了分页参数
        if (nurseItemDTO.getPageNo() != null) {
            current = nurseItemDTO.getPageNo();
        }
        if (nurseItemDTO.getPageSize() != null) {
            size = nurseItemDTO.getPageSize();
        }

        Page<Nursecontent> page = new Page<>(current, size);

        QueryWrapper<Nursecontent> qw = new QueryWrapper<>();
        if (nurseItemDTO.getItemName() != null &&
                StringUtils.isNotBlank(nurseItemDTO.getItemName())) {
            qw.like("nursing_name", "%" + nurseItemDTO.getItemName() + "%");
        }
        if (nurseItemDTO.getStatus() != null) {
            qw.eq("status", nurseItemDTO.getStatus());
        }
        qw.eq("is_deleted", 0); // 只查询未删除数据

        nursecontentservice.page(page, qw);
        return ResultVo.ok(page);
    }

    @ApiOperation("修改护理项目")
    @PostMapping("/updateNurseItem")
    public ResultVo updateNurseItem(@RequestBody Nursecontent nursecontent) throws Exception {
        return nursecontentservice.updateNurseItem(nursecontent);
    }

    @ApiOperation("删除护理项目")
    @GetMapping("/delNurseItem")
    public ResultVo delNurseItem(@RequestParam Integer id) throws Exception {
        return nursecontentservice.delNurseItem(id);
    }
}