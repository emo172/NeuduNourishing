package com.neusoft.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.vo.KhxxCustomerVo;
import org.apache.ibatis.annotations.Param;

public interface CustomerMapper extends BaseMapper<Customer> {
    Page<KhxxCustomerVo> selectPageVo(@Param("page") Page<KhxxCustomerVo> page, @Param("customerName")
    String customerName, @Param("manType") Integer  manType, @Param("userId") Integer userId);
}
