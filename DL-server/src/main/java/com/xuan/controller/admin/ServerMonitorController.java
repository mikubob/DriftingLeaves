package com.xuan.controller.admin;

import com.xuan.dto.ServerMonitorQueryDTO;
import com.xuan.result.Result;
import com.xuan.service.IServerMonitorService;
import com.xuan.vo.CpuDetailVO;
import com.xuan.vo.DiskDetailVO;
import com.xuan.vo.DiskIoDetailVO;
import com.xuan.vo.LoadDetailVO;
import com.xuan.vo.MemoryDetailVO;
import com.xuan.vo.NetworkDetailVO;
import com.xuan.vo.OptionVO;
import com.xuan.vo.ServerMonitorOverviewVO;
import com.xuan.vo.ServerMonitorSnapshotVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/server-monitor")
@RequiredArgsConstructor
public class ServerMonitorController {

    private final IServerMonitorService serverMonitorService;

    @GetMapping("/overview")
    public Result<ServerMonitorOverviewVO> overview() {
        log.info("get server monitor overview");
        return Result.success(serverMonitorService.getOverview());
    }

    @GetMapping("/load")
    public Result<LoadDetailVO> load() {
        log.info("get server monitor load detail");
        return Result.success(serverMonitorService.getLoadDetail());
    }

    @GetMapping("/cpu")
    public Result<CpuDetailVO> cpu() {
        log.info("get server monitor cpu detail");
        return Result.success(serverMonitorService.getCpuDetail());
    }

    @GetMapping("/memory")
    public Result<MemoryDetailVO> memory() {
        log.info("get server monitor memory detail");
        return Result.success(serverMonitorService.getMemoryDetail());
    }

    @GetMapping("/disk/options")
    public Result<List<OptionVO>> diskOptions() {
        log.info("get server monitor disk options");
        return Result.success(serverMonitorService.getDiskOptions());
    }

    @GetMapping("/disk")
    public Result<DiskDetailVO> disk(ServerMonitorQueryDTO queryDTO) {
        log.info("get server monitor disk detail: {}", queryDTO);
        return Result.success(serverMonitorService.getDiskDetail(queryDTO));
    }

    @GetMapping("/network/options")
    public Result<List<OptionVO>> networkOptions() {
        log.info("get server monitor network options");
        return Result.success(serverMonitorService.getNetworkOptions());
    }

    @GetMapping("/network")
    public Result<NetworkDetailVO> network(ServerMonitorQueryDTO queryDTO) {
        log.info("get server monitor network detail: {}", queryDTO);
        return Result.success(serverMonitorService.getNetworkDetail(queryDTO));
    }

    @GetMapping("/disk-io/options")
    public Result<List<OptionVO>> diskIoOptions() {
        log.info("get server monitor disk io options");
        return Result.success(serverMonitorService.getDiskIoOptions());
    }

    @GetMapping("/disk-io")
    public Result<DiskIoDetailVO> diskIo(ServerMonitorQueryDTO queryDTO) {
        log.info("get server monitor disk io detail: {}", queryDTO);
        return Result.success(serverMonitorService.getDiskIoDetail(queryDTO));
    }

    @GetMapping("/snapshot")
    public Result<ServerMonitorSnapshotVO> snapshot(ServerMonitorQueryDTO queryDTO) {
        log.info("get server monitor snapshot: {}", queryDTO);
        return Result.success(serverMonitorService.getSnapshot(queryDTO));
    }
}
