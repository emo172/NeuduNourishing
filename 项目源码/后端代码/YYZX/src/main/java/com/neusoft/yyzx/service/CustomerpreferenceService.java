package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.CustomerPreferenceDTO;
import com.neusoft.yyzx.pojo.Customerpreference;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CustomerPreferenceVo;

public interface CustomerpreferenceService extends IService<Customerpreference> {
    ResultVo<Page<CustomerPreferenceVo>> ListCustomerpreferenceVoPage(CustomerPreferenceDTO customerPreferenceDTO) throws Exception;
}