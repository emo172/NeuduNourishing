package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.BedDetailsDTO;
import com.neusoft.yyzx.dto.ExchangeDTO;
import com.neusoft.yyzx.pojo.Beddetails;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.BedDetailsVo;

public interface BeddetailsService extends IService<Beddetails> {

    ResultVo<Page<BedDetailsVo>> listBedDetailsVoPage(BedDetailsDTO bedDetailsDTO) throws Exception;

    ResultVo exchangeBed(ExchangeDTO exchangeDTO) throws Exception;
}
