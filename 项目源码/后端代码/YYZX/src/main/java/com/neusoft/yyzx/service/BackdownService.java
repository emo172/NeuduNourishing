package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.BackdownDTO;
import com.neusoft.yyzx.pojo.Backdown;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.BackdownVo;

public interface BackdownService extends IService<Backdown> {
    ResultVo<Page<BackdownVo>> listBackdownVo(BackdownDTO backdownDTo) throws Exception;

    ResultVo examineBackdown(Backdown backdown) throws Exception;
}