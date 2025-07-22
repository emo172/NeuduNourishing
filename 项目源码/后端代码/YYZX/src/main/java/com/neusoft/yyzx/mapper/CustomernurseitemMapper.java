package com.neusoft.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.pojo.Customernurseitem;
import com.neusoft.yyzx.vo.CustomerNurseItemVo;
import org.apache.ibatis.annotations.Param;

public interface CustomernurseitemMapper extends BaseMapper<Customernurseitem> {
    Page<CustomerNurseItemVo> selectCustomerItemVo(@Param("page") Page<CustomerNurseItemVo> page, @Param("customerId") Integer customerId) throws Exception;
}
