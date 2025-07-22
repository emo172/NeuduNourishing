package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.BedDetailsDTO;
import com.neusoft.yyzx.dto.ExchangeDTO;
import com.neusoft.yyzx.mapper.BedMapper;
import com.neusoft.yyzx.mapper.BeddetailsMapper;
import com.neusoft.yyzx.mapper.CustomerMapper;
import com.neusoft.yyzx.pojo.Bed;
import com.neusoft.yyzx.pojo.Beddetails;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.service.BeddetailsService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.BedDetailsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class BeddetailsServiceImpl extends ServiceImpl<BeddetailsMapper, Beddetails> implements BeddetailsService {

    @Resource
    private BeddetailsMapper beddetailsMapper;
    @Resource
    private BedMapper bedMapper;
    @Resource
    private CustomerMapper customerMapper;

    @Override
    public ResultVo<Page<BedDetailsVo>> listBedDetailsVoPage(BedDetailsDTO bedDetailsDTO) throws Exception {
        Page<BedDetailsVo> page = new Page<>(bedDetailsDTO.getPageSize(), 10);
        beddetailsMapper.selectBedDetailsVo(page, bedDetailsDTO);
        return ResultVo.ok(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo exchangeBed(ExchangeDTO exchangeDTO) throws Exception {
        //查询床位是否可用
        QueryWrapper qw = new QueryWrapper();
        Bed bed = bedMapper.selectById(exchangeDTO.getNewBedId());
        if (bed.getBedStatus() != 1) {
            return ResultVo.fail("该床位已有人");
        }
        //①修改客户旧床位详情信息：is_deleted为1 床位使用结束时间为当前日期
        Beddetails beddetails = new Beddetails();
        beddetails.setId(exchangeDTO.getId());
        beddetails.setIsDeleted(1);
        beddetails.setEndDate(new Date());//结束时间为当前日期
        int row1 = beddetailsMapper.updateById(beddetails);
        //②添加新床位记录的记录
        Beddetails newBeddetails = new Beddetails();
        newBeddetails.setIsDeleted(0);
        newBeddetails.setCustomerId(exchangeDTO.getCustomerId());
        newBeddetails.setBedId(exchangeDTO.getNewBedId());
        newBeddetails.setEndDate(exchangeDTO.getEndDate());
        newBeddetails.setStartDate(new Date());//开始时间为当前日期
        newBeddetails.setBedDetails(exchangeDTO.getBuildingNo() + "#" + bed.getBedNo());
        int row2 = beddetailsMapper.insert(newBeddetails);
        //③修改旧床位的状态为空闲 bed_status=1
        Bed oldBed = new Bed();
        oldBed.setId(exchangeDTO.getOldBedId());
        oldBed.setBedStatus(1);
        int row3 = bedMapper.updateById(oldBed);
        //④修改新床位的状态为有人 bed_status=2
        Bed newdBed = new Bed();
        newdBed.setId(exchangeDTO.getNewBedId());
        newdBed.setBedStatus(2);
        int row4 = bedMapper.updateById(newdBed);
        //⑤修改客户信息- 新房间号  新床位号 楼号
        Customer customer = new Customer();
        customer.setId(exchangeDTO.getCustomerId());
        customer.setBedId(exchangeDTO.getNewBedId());
        customer.setBuildingNo(exchangeDTO.getBuildingNo());
        customer.setRoomNo(exchangeDTO.getNewRoomNo());
        int row5 = customerMapper.updateById(customer);
        if (!(row1 > 0 && row2 > 0 && row3 > 0 && row4 > 0 && row5 > 0)) {
            throw new Exception("床位调换成功");
        }
        return ResultVo.ok("床位调换失败");
    }
}