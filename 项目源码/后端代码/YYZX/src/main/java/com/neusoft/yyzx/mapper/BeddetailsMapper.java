package com.neusoft.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.BedDetailsDTO;
import com.neusoft.yyzx.pojo.Beddetails;
import com.neusoft.yyzx.vo.BedDetailsVo;
import org.apache.ibatis.annotations.Param;

public interface BeddetailsMapper extends BaseMapper<Beddetails> {
    Page<BedDetailsVo> selectBedDetailsVo(@Param("page") Page<BedDetailsVo> page, @Param("detailsDTO") BedDetailsDTO bedDetailsDTO);
}
