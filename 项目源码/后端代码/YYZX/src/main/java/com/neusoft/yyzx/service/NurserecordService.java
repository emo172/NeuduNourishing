package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.NurseRecordDTO;
import com.neusoft.yyzx.pojo.Nurserecord;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.NurseRecordsVo;

public interface NurserecordService extends IService<Nurserecord> {
    ResultVo addNurseRecord(Nurserecord nurserecord) throws Exception;

    ResultVo<Page<NurseRecordsVo>> queryNurseRecordsVo(NurseRecordDTO nurseRecordDTO) throws Exception;
}