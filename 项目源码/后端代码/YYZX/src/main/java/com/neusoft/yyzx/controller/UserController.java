package com.neusoft.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.yyzx.dto.UserDTO;
import com.neusoft.yyzx.pojo.User;
import com.neusoft.yyzx.service.UserService;
import com.neusoft.yyzx.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string", name = "username", value = "用户登录账号", required = true),
            @ApiImplicitParam(dataType = "string", name = "password", value = "用户登录密码", required = true)
    })

    @GetMapping("/login")
    public ResultVo<User> login(String username, String password) throws Exception {
        return userService.login(username, password);
    }

    // 支持按角色(roleId)和真实姓名(nickname)联合查询
    @ApiOperation("按角色和姓名查询用户-分页")
    @GetMapping("/findUserPage")
    public ResultVo<Page<User>> findUserPage(UserDTO userDTO) throws Exception {
        // 分页参数处理
        Integer currentPage = userDTO.getPageNum() != null ? userDTO.getPageNum() : 1;
        Integer size = userDTO.getPageSize() != null ? userDTO.getPageSize() : 10;
        Page<User> page = new Page<>(currentPage, size);

        QueryWrapper<User> qw = new QueryWrapper<>();
        // 按真实姓名模糊查询
        if (userDTO.getNickname() != null && !userDTO.getNickname().isEmpty()) {
            qw.like("nickname", "%" + userDTO.getNickname() + "%");
        }
        // 按角色精确查询（如果传递了roleId）
        if (userDTO.getRoleId() != null) {
            qw.eq("role_id", userDTO.getRoleId());
        }
        // 只查询未删除的用户（状态正常）
        qw.eq("is_deleted", 0);

        userService.page(page, qw);
        return ResultVo.ok(page);
    }


    @ApiOperation("查询全部用户-分页")
    @GetMapping("/findAllUserPage")
    public ResultVo<Page<User>> findAllUserPage(UserDTO userDTO) throws Exception {
        // 使用pageNum和pageSize创建分页对象
        Integer currentPage = userDTO.getPageNum() != null ? userDTO.getPageNum() : 1;
        Integer size = userDTO.getPageSize() != null ? userDTO.getPageSize() : 10;
        Page<User> page = new Page<>(currentPage, size);

        QueryWrapper<User> qw = new QueryWrapper<>();
        userService.page(page, qw);
        return ResultVo.ok(page);
    }

    @ApiOperation("添加用户")
    @PostMapping("/addUser")
    public ResultVo addUser(@RequestBody User user) throws Exception {
        user.setIsDeleted(0);
        userService.save(user);
        return ResultVo.ok("添加成功");
    }

    @ApiOperation("修改用户")
    @PostMapping("/updateUser")
    public ResultVo updateUser(@RequestBody User user) throws Exception {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        userService.update(user, updateWrapper);
        return ResultVo.ok("修改成功");
    }

    @ApiOperation("删除用户")
    @GetMapping("/delUser")
    public ResultVo delUser(@RequestParam Integer id) throws Exception {
        userService.removeById(id);
        return ResultVo.ok("删除成功");
    }
}