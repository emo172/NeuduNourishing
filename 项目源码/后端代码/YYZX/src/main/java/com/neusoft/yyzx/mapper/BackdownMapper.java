package com.neusoft.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.pojo.Backdown;
import com.neusoft.yyzx.vo.BackdownVo;
import org.apache.ibatis.annotations.Param;

public interface BackdownMapper extends BaseMapper<Backdown> {
    Page<BackdownVo> selectBackdownVo(@Param("page") Page<BackdownVo> page, @Param("userId") Integer userId);
}