package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.pojo.User;
import com.neusoft.yyzx.utils.ResultVo;

public interface UserService extends IService<User> {
    public ResultVo<User> login(String username, String password) throws Exception;
}