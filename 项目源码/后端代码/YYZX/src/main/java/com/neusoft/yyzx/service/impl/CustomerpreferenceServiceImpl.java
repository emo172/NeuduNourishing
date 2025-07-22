package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.CustomerPreferenceDTO;
import com.neusoft.yyzx.mapper.CustomerpreferenceMapper;
import com.neusoft.yyzx.pojo.Customerpreference;
import com.neusoft.yyzx.service.CustomerpreferenceService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CustomerPreferenceVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CustomerpreferenceServiceImpl extends ServiceImpl<CustomerpreferenceMapper, Customerpreference> implements CustomerpreferenceService {
    @Resource
    private CustomerpreferenceMapper customerpreferenceMapper;

    @Override
    public ResultVo<Page<CustomerPreferenceVo>> ListCustomerpreferenceVoPage(CustomerPreferenceDTO customerPreferenceDTO) throws Exception {
        Page<CustomerPreferenceVo> page = new Page<>(customerPreferenceDTO.getPageSize(), 6);
        customerpreferenceMapper.selectCustomerPreferenceVo(page, customerPreferenceDTO.getCustomerName());
        return ResultVo.ok(page);
    }
}