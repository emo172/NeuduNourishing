package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.mapper.MenuMapper;
import com.neusoft.yyzx.mapper.RolemenuMapper;
import com.neusoft.yyzx.mapper.UserMapper;
import com.neusoft.yyzx.pojo.Menu;
import com.neusoft.yyzx.pojo.User;
import com.neusoft.yyzx.service.UserService;
import com.neusoft.yyzx.utils.ResultVo;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RolemenuMapper rolemenuMapper;
    @Resource
    private MenuMapper menuMapper;

    public ResultVo<User> login(String username, String password) throws Exception {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("username", username);
        qw.eq("password", password);
        User user = getOne(qw);
        System.out.println(user);
        if (user != null) {
            if (user.getIsDeleted() == 0) {
                //根据用户角色获取当前用户的菜单
                //①获取得到角色对应的menu_id
                QueryWrapper listRoleQw = new QueryWrapper<>();
                listRoleQw.eq("role_id", user.getRoleId());
                listRoleQw.select("menu");
                List<Integer> menuIds = rolemenuMapper.selectObjs(listRoleQw);
                //②根据menu_id查询一级菜单列表
                List<Menu> menus = menuMapper.selectBatchIds(menuIds);
                //③查询子菜单
                for (Menu menu : menus) {
                    QueryWrapper listMenuQw = new QueryWrapper<>();
                    listMenuQw.eq("parent_id", menu.getId());
                    menu.setChildren(menuMapper.selectList(listMenuQw));
                }
                user.setMenuList(menus);
                HashMap<String, Object> map = new HashMap<>();
                //如果登录验证成功，则需要生成令牌token（token就是按照特定规则生成的字符串）
                JwtBuilder builder = Jwts.builder();
                String token = builder.setSubject(username) //主题，就是token中携带的数据
                        .setIssuedAt(new Date()) //设置token的生成时间
                        .setId(user.getId().toString()) //设置用户id为token id
                        .setClaims(map)  //map中可以存放用户的⻆色权限信息
                        .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))//设置过期时间
                        .signWith(SignatureAlgorithm.HS256, "neuedu123456")//设置加密方式和加密密码
                        .compact();
                return ResultVo.ok(user, token);
            }
            return ResultVo.fail("无权限，请联系管理员");
        }
        return ResultVo.fail("账号密码错误");
    }
}