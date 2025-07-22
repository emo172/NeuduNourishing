package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.CustomerNurseItemDTO;
import com.neusoft.yyzx.mapper.CustomerMapper;
import com.neusoft.yyzx.mapper.CustomernurseitemMapper;
import com.neusoft.yyzx.mapper.NursecontentMapper;
import com.neusoft.yyzx.pojo.Customer;
import com.neusoft.yyzx.pojo.Customernurseitem;
import com.neusoft.yyzx.service.CustomernurseitemService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CustomerNurseItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CustomernurseitemServiceImpl extends ServiceImpl<CustomernurseitemMapper, Customernurseitem> implements CustomernurseitemService {
    private static final Logger log = LoggerFactory.getLogger(CustomernurseitemServiceImpl.class);

    @Resource
    private CustomernurseitemMapper customernurseitemMapper;
    @Resource
    private NursecontentMapper nursecontentMapper;
    @Resource
    private CustomerMapper customerMapper;

    @Override
    public ResultVo addItemToCustomer(Integer customerId, List<Customernurseitem> customernurseitems) throws Exception {
        if (customernurseitems.size() > 0) {
            // 确保所有护理项目都设置了客户ID
            for (Customernurseitem item : customernurseitems) {
                item.setCustomerId(customerId);
            }

            //判断是选择级别添加级别中的护理项目,还是单独购买护理项目
            if (customernurseitems.get(0).getLevelId() != null) { //级别附带项目
                //设置客户护理级别
                Customer customer = new Customer();
                customer.setId(customerId);
                customer.setLevelId(customernurseitems.get(0).getLevelId());
                int row1 = customerMapper.updateById(customer);
                //批量给用户添加护理项目
                boolean temp = saveBatch(customernurseitems);
                if (!(row1 > 0 && temp)) {
                    throw new Exception("护理项目配置失败");
                }
            } else { //单独购买护理项目
                boolean temp = saveBatch(customernurseitems);
                if (!temp) {
                    throw new Exception("护理项目配置失败");
                }
            }
            return ResultVo.ok("护理项目配置成功");
        }
        return ResultVo.fail("请选择需要添加的护理项目");
    }

    @Override
    public ResultVo removeCustomerLevelAndItem(Integer levelId, Integer customerId) throws Exception {
        // 记录输入参数
        log.info("尝试移除客户ID:{} 的护理级别ID:{}", customerId, levelId);

        //更新客户级别为null
        UpdateWrapper uw1 = new UpdateWrapper();
        uw1.set("level_id", null);
        uw1.eq("id", customerId);
        int row1 = customerMapper.update(null, uw1);
        log.info("更新客户级别影响行数:{}", row1);

        //删除客户当前级别的所有护理项目
        UpdateWrapper uw2 = new UpdateWrapper();
        uw2.set("is_deleted", 1);
        uw2.eq("level_id", levelId);
        uw2.eq("customer_id", customerId);
        int row2 = customernurseitemMapper.update(null, uw2);
        log.info("更新护理项目状态影响行数:{}", row2);

        if (!(row1 > 0 && row2 > 0)) {
            // 记录详细错误信息
            if(row1 == 0) {
                log.warn("未找到ID为{}的客户记录", customerId);
            }
            if(row2 == 0) {
                log.warn("未找到级别ID为{}且客户ID为{}的护理项目记录", levelId, customerId);
            }
            throw new Exception("护理项目配置失败");
        }
        return ResultVo.ok("移除成功");
    }

    @Override
    public ResultVo<Page<CustomerNurseItemVo>> listCustomerItem(CustomerNurseItemDTO customerNurseItemDTO) throws Exception {
        Page<CustomerNurseItemVo> page = new Page<>(customerNurseItemDTO.getPageSize(), 10                    );
        customernurseitemMapper.selectCustomerItemVo(page, customerNurseItemDTO.getCustomerId());
        return ResultVo.ok(page);
    }
}