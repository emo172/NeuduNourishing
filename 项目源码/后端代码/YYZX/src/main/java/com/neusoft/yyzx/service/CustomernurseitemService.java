package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.CustomerNurseItemDTO;
import com.neusoft.yyzx.pojo.Customernurseitem;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CustomerNurseItemVo;

import java.util.List;

public interface CustomernurseitemService extends IService<Customernurseitem> {
    ResultVo addItemToCustomer(Integer customerId, List<Customernurseitem> customernurseitems) throws Exception;

    ResultVo removeCustomerLevelAndItem(Integer levelId, Integer customerId) throws Exception;

    ResultVo<Page<CustomerNurseItemVo>> listCustomerItem(CustomerNurseItemDTO customerNurseItemDTO) throws Exception;
}