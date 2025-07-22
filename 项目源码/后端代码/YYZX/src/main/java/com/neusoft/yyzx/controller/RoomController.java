package com.neusoft.yyzx.controller;

import com.neusoft.yyzx.pojo.Room;
import com.neusoft.yyzx.service.RoomService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CwsyBedVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/room")
@Api(tags = "房间管理")
@CrossOrigin
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/findCwsyBedVo")
    @ApiOperation("查询床位示意视图数据")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string", name = "floor", value = "楼层", required = true)
    })
    public ResultVo<CwsyBedVo> findCwsyBedVo(String floor) throws Exception {
        return roomService.findCwsyBedVo(floor);
    }

    @GetMapping("/listRoom")
    @ApiOperation("查询房间列表")
    public ResultVo<List<Room>> listRoom() {
        return ResultVo.ok(roomService.list());
    }
}
