package com.xuan.service;

import com.xuan.dto.ServerMonitorQueryDTO;
import com.xuan.vo.CpuDetailVO;
import com.xuan.vo.DiskDetailVO;
import com.xuan.vo.DiskIoDetailVO;
import com.xuan.vo.LoadDetailVO;
import com.xuan.vo.MemoryDetailVO;
import com.xuan.vo.NetworkDetailVO;
import com.xuan.vo.OptionVO;
import com.xuan.vo.ServerMonitorOverviewVO;
import com.xuan.vo.ServerMonitorSnapshotVO;

import java.util.List;

public interface IServerMonitorService {

    ServerMonitorOverviewVO getOverview();

    LoadDetailVO getLoadDetail();

    CpuDetailVO getCpuDetail();

    MemoryDetailVO getMemoryDetail();

    List<OptionVO> getDiskOptions();

    DiskDetailVO getDiskDetail(ServerMonitorQueryDTO queryDTO);

    List<OptionVO> getNetworkOptions();

    NetworkDetailVO getNetworkDetail(ServerMonitorQueryDTO queryDTO);

    List<OptionVO> getDiskIoOptions();

    DiskIoDetailVO getDiskIoDetail(ServerMonitorQueryDTO queryDTO);

    ServerMonitorSnapshotVO getSnapshot(ServerMonitorQueryDTO queryDTO);
}
