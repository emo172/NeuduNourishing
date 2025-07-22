package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.pojo.Nursecontent;
import com.neusoft.yyzx.utils.ResultVo;

import java.util.List;

public interface NursecontentService extends IService<Nursecontent> {
    ResultVo<List<Nursecontent>> listNurseItemByLevel(Integer levelId) throws Exception;

    ResultVo updateNurseItem(Nursecontent nursecontent) throws Exception;

    ResultVo delNurseItem(Integer id) throws Exception;
}