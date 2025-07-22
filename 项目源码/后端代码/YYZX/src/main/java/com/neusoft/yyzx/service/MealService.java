package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.dto.MealDTO;
import com.neusoft.yyzx.pojo.Meal;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.MealVo;

public interface MealService extends IService<Meal> {
    ResultVo<Page<MealVo>> listMealVoPage(MealDTO mealDTO) throws Exception;
}