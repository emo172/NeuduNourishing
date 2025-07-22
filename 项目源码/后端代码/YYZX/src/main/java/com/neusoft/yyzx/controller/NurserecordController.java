package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.NurseRecordDTO;
import com.neusoft.yyzx.dto.OutwardDTO;
import com.neusoft.yyzx.pojo.Nurserecord;
import com.neusoft.yyzx.service.NurserecordService;
import com.neusoft.yyzx.service.OutwardService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.NurseRecordsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nurserecord")
@CrossOrigin
@Api(tags = "护理记录管理")
public class NurserecordController {

    @Autowired
    private NurserecordService nurserecordService;

    @Autowired
    private OutwardService outwardService;

    @ApiOperation("添加护理记录")
    @PostMapping("/addNurseRecord")
    public ResultVo addNurseRecord(@RequestBody Nurserecord nurserecord) throws Exception {
        return nurserecordService.addNurseRecord(nurserecord);
    }

    @ApiOperation("客户护理记录信息动态查询（分页）")
    @GetMapping("/listNurseRecordsVo")
    public ResultVo<Page<NurseRecordsVo>> listNurseRecordsVo(NurseRecordDTO nurseRecordDTO) throws Exception {
        return nurserecordService.queryNurseRecordsVo(nurseRecordDTO);
    }

    @ApiOperation("软删除护理记录")
    @GetMapping("/softDeleteCustomerRecord")
    public ResultVo softDeleteCustomerRecord(@RequestParam Integer id) {
        Nurserecord nurseRecord = new Nurserecord();
        nurseRecord.setIsDeleted(1);
        nurseRecord.setId(id);
        nurserecordService.updateById(nurseRecord);
        return ResultVo.ok("移除成功");
    }

    @ApiOperation("查询外出记录")
    @PostMapping("/queryOutwardVo")
    public ResultVo queryOutwardVo(@RequestBody OutwardDTO outwardDTO) throws Exception {
        return outwardService.queryOutwardVo(outwardDTO);
    }
}
