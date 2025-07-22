package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.mapper.NursecontentMapper;
import com.neusoft.yyzx.mapper.NurselevelitemMapper;
import com.neusoft.yyzx.pojo.Nursecontent;
import com.neusoft.yyzx.service.NursecontentService;
import com.neusoft.yyzx.utils.ResultVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class NursecontentServiceImpl extends ServiceImpl<NursecontentMapper, Nursecontent> implements NursecontentService {
    @Resource
    private NursecontentMapper nursecontentMapper;
    @Resource
    private NurselevelitemMapper nurselevelitemMapper;

    @Override
    public ResultVo<List<Nursecontent>> listNurseItemByLevel(Integer levelId) throws Exception {
        //①先查询级别的项目配置-只查询item_id
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("level_id", levelId);
        qw.select("item_id");
        List<Integer> itemIds = nurselevelitemMapper.selectObjs(qw);
        List<Nursecontent> nursecontents = new ArrayList<>();
        //判断是否有记录
        if (itemIds.size() > 0) {
            //②查询护理项目信息
            nursecontents = nursecontentMapper.selectBatchIds(itemIds);
        }
        return ResultVo.ok(nursecontents);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVo updateNurseItem(Nursecontent nursecontent) throws Exception {
        //如果修改状态为-一停用，需要直接剔除护理级别护理项目列表中的对应的记录，保证列表中的项目都是可用状态
        if (nursecontent.getStatus() == 2) {
            //查询当前护理项目是否在护理级别护理项目列表中，如果在就需要进行剔除
            QueryWrapper qwCount = new QueryWrapper();
            qwCount.eq("item_id", nursecontent.getId());
            int count = nurselevelitemMapper.selectCount(qwCount);
            if (count > 0) {
                QueryWrapper qw = new QueryWrapper();
                qw.eq("item_id", nursecontent.getId());
                int row = nurselevelitemMapper.delete(qw);
                //更新护理项目
                boolean temp = updateById(nursecontent);
                if (!(temp && row > 0)) {
                    throw new Exception("更新失败");
                }
                return ResultVo.ok("更新成功");
            }
        }
        //更新护理项目
        updateById(nursecontent);
        return ResultVo.ok("更新成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVo delNurseItem(Integer id) throws Exception {
        Nursecontent nursecontent = new Nursecontent();
        nursecontent.setIsDeleted(1);
        nursecontent.setId(id);
        //查询当前护理项目是否在护理级别护理项目列表中，如果在就需要进行剔除
        QueryWrapper qwCount = new QueryWrapper();
        qwCount.eq("item_id", id);
        int count = nurselevelitemMapper.selectCount(qwCount);
        if (count > 0) {
            QueryWrapper qw = new QueryWrapper();
            qw.eq("item_id", id);
            int row = nurselevelitemMapper.delete(qw);
            //更新逻辑删除标志为“1”隐藏
            boolean temp = updateById(nursecontent);
            if (!(temp && row > 0)) {
                throw new Exception("更新失败");
            }
            return ResultVo.ok("更新成功");
        }
        //更新逻辑删除标志为“1”隐藏
        updateById(nursecontent);
        return ResultVo.ok("更新成功");
    }
}