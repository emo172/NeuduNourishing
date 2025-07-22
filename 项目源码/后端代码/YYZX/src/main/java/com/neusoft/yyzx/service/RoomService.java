package com.neusoft.yyzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.yyzx.pojo.Room;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CwsyBedVo;

public interface RoomService extends IService<Room> {
    public ResultVo<CwsyBedVo> findCwsyBedVo(String floor) throws Exception;
}