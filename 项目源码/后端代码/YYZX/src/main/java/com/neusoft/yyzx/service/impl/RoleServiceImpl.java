package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.mapper.RoleMapper;
import com.neusoft.yyzx.pojo.Role;
import com.neusoft.yyzx.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
