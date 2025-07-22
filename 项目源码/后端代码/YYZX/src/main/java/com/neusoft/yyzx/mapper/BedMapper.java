package com.neusoft.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.yyzx.pojo.Bed;
import com.neusoft.yyzx.vo.CwsyBedVo;

public interface BedMapper extends BaseMapper<Bed> {
    CwsyBedVo selectBedCount();
}
