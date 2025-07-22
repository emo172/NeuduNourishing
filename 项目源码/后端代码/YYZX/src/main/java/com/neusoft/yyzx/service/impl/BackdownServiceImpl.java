package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.BackdownDTO;
import com.neusoft.yyzx.mapper.BackdownMapper;
import com.neusoft.yyzx.pojo.Backdown;
import com.neusoft.yyzx.service.BackdownService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.BackdownVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BackdownServiceImpl extends ServiceImpl<BackdownMapper, Backdown> implements BackdownService {
    @Resource
    private BackdownMapper backdownMapper;

    @Override
    public ResultVo<Page<BackdownVo>> listBackdownVo(BackdownDTO backdownDTo) throws Exception {
        Page<BackdownVo> page = new Page<>(backdownDTo.getPageSize(), 6);
        backdownMapper.selectBackdownVo(page, backdownDTo.getUserId());
        return ResultVo.ok(page);
    }

    @Override
    public ResultVo
    examineBackdown(Backdown backdown) throws Exception {
        UpdateWrapper<Backdown> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", backdown.getId());
        updateWrapper.set("auditstatus", backdown.getAuditstatus());
        updateWrapper.set("audittime", backdown.getAudittime());
        updateWrapper.set("auditperson", backdown.getAuditperson());
        backdownMapper.update(backdown, updateWrapper);
        return ResultVo.ok("审批成功");
    }
}