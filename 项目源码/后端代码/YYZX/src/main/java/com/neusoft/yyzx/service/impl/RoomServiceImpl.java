package com.neusoft.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.yyzx.mapper.BedMapper;
import com.neusoft.yyzx.mapper.RoomMapper;
import com.neusoft.yyzx.pojo.Room;
import com.neusoft.yyzx.service.RoomService;
import com.neusoft.yyzx.utils.ResultVo;
import com.neusoft.yyzx.vo.CwsyBedVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Resource
    private BedMapper bedMapper;

    @Resource
    private RoomMapper roomMapper;

    @Override
    public ResultVo<CwsyBedVo> findCwsyBedVo(String floor) {
        // 统计床位
        CwsyBedVo cwsyBedVo = bedMapper.selectBedCount();

        // 查询所有房间
        QueryWrapper qwRoomList = new QueryWrapper();
        qwRoomList.eq("room_floor", floor);
        List<Room> rooms = list(qwRoomList);

        for (Room room : rooms) {
            // 查询每个房间的床位信息
            QueryWrapper qwBedList = new QueryWrapper();
            qwBedList.eq("room_no", room.getRoomNo());
            room.setBedList(bedMapper.selectList(qwBedList));
        }

        cwsyBedVo.setRoomList(rooms);
        return ResultVo.ok(cwsyBedVo);
    }
}