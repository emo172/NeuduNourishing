package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.NurseRecordDTO;
import com.neusoft.yyzx.mapper.CustomernurseitemMapper;
import com.neusoft.yyzx.mapper.NurserecordMapper;
import com.neusoft.yyzx.pojo.Customernurseitem;
import com.neusoft.yyzx.pojo.Nurserecord;
import com.neusoft.yyzx.service.NurserecordService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.NurseRecordsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class NurserecordServiceImpl extends ServiceImpl<NurserecordMapper, Nurserecord> implements NurserecordService {
    private static final Logger logger = LoggerFactory.getLogger(NurserecordServiceImpl.class);

    @Resource
    private CustomernurseitemMapper customernurseitemMapper;
    @Resource
    private NurserecordMapper nurserecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo addNurseRecord(Nurserecord nurserecord) throws Exception {
        // 生成护理记录
        nurserecord.setIsDeleted(0); // 默认生效
        boolean temp = save(nurserecord);

        // 查询当前用户的护理项目余量
        QueryWrapper<Customernurseitem> qw = new QueryWrapper<>();
        qw.eq("customer_id", nurserecord.getCustomerId());
        qw.eq("item_id", nurserecord.getItemId());
        qw.eq("is_deleted", 0);
        Customernurseitem customernurseitem = customernurseitemMapper.selectOne(qw);

        if (customernurseitem == null) {
            logger.error("未找到客户护理项目: customerId={}, itemId={}",
                    nurserecord.getCustomerId(), nurserecord.getItemId());
            throw new Exception("未找到对应的护理项目");
        }

        // 修改用户护理项目数量
        UpdateWrapper<Customernurseitem> uw = new UpdateWrapper<>();
        uw.set("nurse_number", customernurseitem.getNurseNumber() - nurserecord.getNursingCount()); // 余量 - 当前护理数量
        uw.eq("customer_id", nurserecord.getCustomerId());
        uw.eq("item_id", nurserecord.getItemId());
        uw.eq("is_deleted", 0);
        int row = customernurseitemMapper.update(null, uw);

        if (!(temp && row > 0)) {
            throw new Exception("护理记录生成失败");
        }
        return ResultVo.ok("护理记录生成成功");
    }

    @Override
    public ResultVo<Page<NurseRecordsVo>> queryNurseRecordsVo(NurseRecordDTO nurseRecordDTO) throws Exception {
        // 1. 处理DTO为null的情况
        if (nurseRecordDTO == null) {
            logger.warn("queryNurseRecordsVo received null DTO, using default parameters");
            nurseRecordDTO = new NurseRecordDTO();
        }

        // 2. 安全处理分页参数
        int currentPage = (nurseRecordDTO.getPageSize() != null && nurseRecordDTO.getPageSize() > 0)
                ? nurseRecordDTO.getPageSize() : 1;

        // 3. 每页大小固定为6
        int pageSize = 10;

        // 4. 创建分页对象
        Page<NurseRecordsVo> page = new Page<>(currentPage, pageSize);

        // 5. 安全处理客户ID
        Integer customerId = nurseRecordDTO.getCustomerId();

        // 6. 执行查询
        nurserecordMapper.selectNurseRecordsVo(page, customerId);

        return ResultVo.ok(page);
    }
}