package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.KhxxDTO;
import com.neusoft.yyzx.mapper.BedMapper;
import com.neusoft.yyzx.mapper.BeddetailsMapper;
import com.neusoft.yyzx.mapper.CustomerMapper;
import com.neusoft.yyzx.pojo.Bed;
import com.neusoft.yyzx.pojo.Beddetails;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.service.CustomerService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.KhxxCustomerVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyh
 * @since 2023-11-07
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Resource
    private BeddetailsMapper beddetailsMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private BedMapper bedMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo addCustomer(Customer customer) throws Exception {
        // 校验身份证号
        if (Objects.isNull(customer.getIdcard()) || customer.getIdcard().trim().isEmpty()) {
            return ResultVo.fail("身份证号不能为空");
        }

        // 查询床位是否可用
        Bed bed = bedMapper.selectById(customer.getBedId());
        if (bed == null || bed.getBedStatus() != 1) {
            return ResultVo.fail("该床位已占用或不存在");
        }

        customer.setIsDeleted(0); // 新增客户默认生效
        customer.setUserId(-1); // 新增客户默认无健康管家

        // 插入客户信息
        int row1 = customerMapper.insert(customer);
        // 插入床位详情
        Beddetails beddetails = new Beddetails();
        beddetails.setBedId(customer.getBedId());
        beddetails.setStartDate(customer.getCheckinDate());
        beddetails.setEndDate(customer.getExpirationDate());
        beddetails.setBedDetails(customer.getBuildingNo() + "#" + bed.getBedNo());
        beddetails.setCustomerId(customer.getId());
        beddetails.setIsDeleted(0); // 床位详情生效
        int row2 = beddetailsMapper.insert(beddetails);
        // 更新床位状态为占用
        Bed bedUpdate = new Bed();
        bedUpdate.setId(customer.getBedId());
        bedUpdate.setBedStatus(2);
        int row3 = bedMapper.updateById(bedUpdate);

        if (!(row1 > 0 && row2 > 0 && row3 > 0)) {
            throw new Exception("入住失败");
        }
        return ResultVo.ok("入住成功");
    }

    @Override
    public ResultVo<Page<KhxxCustomerVo>> khxxFindCustomer(KhxxDTO khxxDTO) throws Exception {
        // 分页参数默认值
        if (khxxDTO.getPageNum() == null || khxxDTO.getPageNum() <= 0) {
            khxxDTO.setPageNum(1);
        }
        if (khxxDTO.getPageSize() == null || khxxDTO.getPageSize() <= 0) {
            khxxDTO.setPageSize(10);
        }

        Page<KhxxCustomerVo> page = new Page<>(khxxDTO.getPageNum(), khxxDTO.getPageSize());
        customerMapper.selectPageVo(page,
                khxxDTO.getCustomerName(),
                khxxDTO.getManType(),
                khxxDTO.getUserId()
        );
        return ResultVo.ok(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo removeCustomer(Integer id, Integer bedId) throws Exception {
        // 1. 标记客户为删除
        Customer customer = new Customer();
        customer.setId(id);
        customer.setIsDeleted(1);
        int row1 = customerMapper.updateById(customer);

        // 2. 标记床位为空闲
        Bed bed = new Bed();
        bed.setId(bedId);
        bed.setBedStatus(1);
        int row2 = bedMapper.updateById(bed);

        // 3. 标记床位详情为删除（允许无匹配记录）
        Beddetails beddetails = new Beddetails();
        beddetails.setIsDeleted(1);
        UpdateWrapper<Beddetails> uw = new UpdateWrapper<>();
        uw.eq("customer_id", id)
                .eq("bed_id", bedId)
                .eq("is_deleted", 0);
        int row3 = beddetailsMapper.update(beddetails, uw);

        // 核心修改：只要客户和床位状态更新成功，就认为删除成功（放宽row3的限制）
        if (!(row1 > 0 && row2 > 0)) {
            throw new Exception("删除失败");
        }
        return ResultVo.ok("删除成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo editCustomer(Customer customer) throws Exception {
        // 更新客户信息
        int row1 = customerMapper.updateById(customer);
        // 若合同到期时间变更，同步更新床位详情
        if (customer.getExpirationDate() != null) {
            UpdateWrapper<Beddetails> uw = new UpdateWrapper<>();
            uw.eq("customer_id", customer.getId())
                    .eq("is_deleted", 0);
            Beddetails beddetails = new Beddetails();
            beddetails.setEndDate(customer.getExpirationDate());
            int row2 = beddetailsMapper.update(beddetails, uw);
            if (!(row1 > 0 && row2 > 0)) {
                throw new Exception("编辑失败");
            }
        } else {
            if (row1 <= 0) {
                throw new Exception("编辑失败");
            }
        }
        return ResultVo.ok("编辑成功");
    }
}