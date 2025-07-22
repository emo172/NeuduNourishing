package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.OutwardDTO;
import com.neusoft.yyzx.pojo.Outward;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.OutwardVo;

public interface OutwardService extends IService<Outward> {
    ResultVo examineOutward(Outward outward) throws Exception;

    ResultVo<Page<OutwardVo>> queryOutwardVo(OutwardDTO outwardDTO) throws Exception;
}
