package com.neusoft.yyzx.controller;

import com.neusoft.yyzx.pojo.Food;
import com.neusoft.yyzx.service.FoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/food")
@CrossOrigin
@Api(tags = "食品管理")
public class FoodController {
    @Resource
    private FoodService foodService;

    @GetMapping("/listFood")
    @ApiOperation("查询所有食品列表")
    public List<Food> listFood() throws Exception {
        return foodService.list();
    }
}
