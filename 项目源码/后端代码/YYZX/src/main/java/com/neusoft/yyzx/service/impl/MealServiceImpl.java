package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.dto.MealDTO;
import com.neusoft.yyzx.mapper.MealMapper;
import com.neusoft.yyzx.pojo.Meal;
import com.neusoft.yyzx.service.MealService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.MealVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MealServiceImpl extends ServiceImpl<MealMapper, Meal> implements MealService {
    @Resource
    private MealMapper mealMapper;

    @Override
    public ResultVo<Page<MealVo>> listMealVoPage(MealDTO mealDTO) throws Exception {
        Page<MealVo> page = new Page<>(mealDTO.getPageNum(), mealDTO.getPageSize());
        mealMapper.selectMealVo(page, mealDTO.getWeekDay(), mealDTO.getMealType());
        return ResultVo.ok(page);
    }
}