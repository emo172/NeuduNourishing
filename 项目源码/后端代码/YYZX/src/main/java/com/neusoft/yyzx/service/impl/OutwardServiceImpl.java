package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.OutwardDTO;
import com.neusoft.yyzx.mapper.OutwardMapper;
import com.neusoft.yyzx.pojo.Outward;
import com.neusoft.yyzx.service.OutwardService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.OutwardVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OutwardServiceImpl extends ServiceImpl<OutwardMapper, Outward> implements OutwardService {
    @Resource
    private OutwardMapper outwardMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo examineOutward(Outward outward) throws Exception {
        UpdateWrapper<Outward> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", outward.getId());
        updateWrapper.set("auditstatus", outward.getAuditstatus());
        outwardMapper.update(outward, updateWrapper);
        return ResultVo.ok("审批成功");
    }

    @Override
    public ResultVo<Page<OutwardVo>> queryOutwardVo(OutwardDTO outwardDTO) throws Exception {
        // 1. 检查参数是否为null
        if (outwardDTO == null) {
            return ResultVo.fail("查询参数不能为空");
        }

        // 2. 处理分页参数，设置默认值
        Integer pageSize = outwardDTO.getPageSize();
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10; // 设置默认分页大小
        }

        // 3. 处理userId参数
        Integer userId = outwardDTO.getUserId();
        if (userId == null) {
            return ResultVo.fail("用户编号不能为空");
        }

        // 4. 创建分页对象
        Page<OutwardVo> page = new Page<>(1, pageSize); // 默认为第一页

        // 5. 调用Mapper方法
        outwardMapper.selectOutwardVo(page, userId);

        return ResultVo.ok(page);
    }
}