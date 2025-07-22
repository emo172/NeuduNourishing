package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.mapper.FoodMapper;
import com.neusoft.yyzx.pojo.Food;
import com.neusoft.yyzx.service.FoodService;
import org.springframework.stereotype.Service;

@Service
public class FoodServiceImp extends ServiceImpl<FoodMapper, Food> implements FoodService {
}
