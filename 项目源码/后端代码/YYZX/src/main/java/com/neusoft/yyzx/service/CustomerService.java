package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.KhxxDTO;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.KhxxCustomerVo;

public interface CustomerService extends IService<Customer> {

    ResultVo addCustomer(Customer customer) throws Exception;

    ResultVo<Page<KhxxCustomerVo>> khxxFindCustomer(KhxxDTO khxxDTO) throws Exception;

    ResultVo removeCustomer(Integer id, Integer bedId) throws Exception;

    ResultVo editCustomer(Customer customer) throws Exception;
}