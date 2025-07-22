package com.neusoft.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.pojo.Nurserecord;
import com.neusoft.yyzx.vo.NurseRecordsVo;
import org.apache.ibatis.annotations.Param;

public interface NurserecordMapper extends BaseMapper<Nurserecord> {
    Page<NurseRecordsVo> selectNurseRecordsVo(@Param("page") Page<NurseRecordsVo> page, @Param("customerId") Integer customerId);
}
