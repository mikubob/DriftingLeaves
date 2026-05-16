package com.xuan.service.impl.monitor;

import com.xuan.constant.MonitorConstant;
import com.xuan.constant.RedisConstant;
import com.xuan.dto.ServerMonitorQueryDTO;
import com.xuan.enumeration.DeploymentModeEnum;
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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 服务监控查询服务实现
 * <p>
 * 该类只负责：
 * 1. 从 Redis 读取 current 快照与趋势点
 * 2. 根据查询参数选择对应资源
 * 3. 按部署模式做轻量语义修正
 * 4. 组装前端主消费的 snapshot 聚合结果
 * <p>
 * 它不参与实时采样，真正的采样统一由定时任务写入缓存。
 */
@Service
@RequiredArgsConstructor
public class ServerMonitorServiceImpl implements IServerMonitorService {

    /**
     * 监控缓存读取器
     */
    private final ServerMonitorCacheReader cacheReader;

    /**
     * 部署环境识别工具
     */
    private final ServerMonitorEnvironmentUtil environmentUtil;

    @Override
    public ServerMonitorOverviewVO getOverview() {
        return readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_OVERVIEW, ServerMonitorOverviewVO.class,
                ServerMonitorOverviewVO.builder().build());
    }

    @Override
    public LoadDetailVO getLoadDetail() {
        return readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_LOAD, LoadDetailVO.class,
                LoadDetailVO.builder().topProcesses(Collections.emptyList()).build());
    }

    @Override
    public CpuDetailVO getCpuDetail() {
        return readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_CPU, CpuDetailVO.class,
                CpuDetailVO.builder().coreUsages(Collections.emptyList()).topProcesses(Collections.emptyList()).build());
    }

    @Override
    public MemoryDetailVO getMemoryDetail() {
        MemoryDetailVO detailVO = readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_MEMORY, MemoryDetailVO.class,
                MemoryDetailVO.builder().topProcesses(Collections.emptyList()).build());

        // 容器模式下 shared/buffer cache 往往无法稳定表达真实宿主机语义，
        // 因此宁可返回 null，也不返回可能误导前端的数据。
        if (environmentUtil.isContainerMode()) {
            detailVO.setSharedBytes(null);
            detailVO.setSharedText(null);
            detailVO.setBufferCacheBytes(null);
            detailVO.setBufferCacheText(null);
        }
        return detailVO;
    }

    @Override
    public List<OptionVO> getDiskOptions() {
        // 容器模式下会额外过滤掉 /etc/hosts 等典型噪音挂载点
        return filterDiskOptions(cacheReader.readOptions(RedisConstant.SERVER_MONITOR_DISK_OPTIONS));
    }

    @Override
    public DiskDetailVO getDiskDetail(ServerMonitorQueryDTO queryDTO) {
        String resourceName = normalizeDiskResourceName(queryDTO);
        DiskDetailVO detailVO = readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_DISK_PREFIX + resourceName, DiskDetailVO.class,
                DiskDetailVO.builder().resourceName(resourceName).build());

        // 对容器注入型挂载点，inode 统计通常不具备良好解释价值，统一按不可用处理。
        if (environmentUtil.isContainerMode() && environmentUtil.isContainerNoiseMount(detailVO.getResourceName())) {
            detailVO.setInode(null);
        }
        return detailVO;
    }

    @Override
    public List<OptionVO> getNetworkOptions() {
        return cacheReader.readOptions(RedisConstant.SERVER_MONITOR_NETWORK_OPTIONS);
    }

    @Override
    public NetworkDetailVO getNetworkDetail(ServerMonitorQueryDTO queryDTO) {
        String resourceName = normalizeNetworkResourceName(queryDTO);
        NetworkDetailVO detailVO = readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_NETWORK_PREFIX + resourceName, NetworkDetailVO.class,
                NetworkDetailVO.builder().resourceName(resourceName).build());
        detailVO.setPoints(cacheReader.readPoints(RedisConstant.SERVER_MONITOR_NETWORK_TIMELINE_PREFIX + resourceName, normalizeLimit(queryDTO)));
        return detailVO;
    }

    @Override
    public List<OptionVO> getDiskIoOptions() {
        return cacheReader.readOptions(RedisConstant.SERVER_MONITOR_DISK_IO_OPTIONS);
    }

    @Override
    public DiskIoDetailVO getDiskIoDetail(ServerMonitorQueryDTO queryDTO) {
        String resourceName = normalizeDiskIoResourceName(queryDTO);
        DiskIoDetailVO detailVO = readCurrent(RedisConstant.SERVER_MONITOR_CURRENT_DISK_IO_PREFIX + resourceName, DiskIoDetailVO.class,
                DiskIoDetailVO.builder().resourceName(resourceName).build());
        detailVO.setPoints(cacheReader.readPoints(RedisConstant.SERVER_MONITOR_DISK_IO_TIMELINE_PREFIX + resourceName, normalizeLimit(queryDTO)));
        return detailVO;
    }

    @Override
    public ServerMonitorSnapshotVO getSnapshot(ServerMonitorQueryDTO queryDTO) {
        // 1. 先解析当前部署模式，后续所有提示文案都围绕这个模式组装
        DeploymentModeEnum deploymentMode = environmentUtil.resolveDeploymentMode();

        // 2. 读取各模块 current 快照
        ServerMonitorOverviewVO overview = getOverview();
        LoadDetailVO load = getLoadDetail();
        CpuDetailVO cpu = getCpuDetail();
        MemoryDetailVO memory = getMemoryDetail();
        DiskDetailVO disk = getDiskDetail(queryDTO);
        NetworkDetailVO network = getNetworkDetail(queryDTO);
        DiskIoDetailVO diskIo = getDiskIoDetail(queryDTO);

        // 3. 组装前端主消费的聚合快照
        return ServerMonitorSnapshotVO.builder()
                .collectTime(firstNonBlank(
                        overview.getCollectTime(),
                        load.getCollectTime(),
                        cpu.getCollectTime(),
                        memory.getCollectTime(),
                        disk.getCollectTime(),
                        network.getCollectTime(),
                        diskIo.getCollectTime()))
                .deploymentMode(deploymentMode.getValue())
                .deploymentModeText(environmentUtil.buildDeploymentModeText(deploymentMode))
                .deploymentTips(environmentUtil.buildDeploymentTips(deploymentMode))
                .overview(overview)
                .load(load)
                .cpu(cpu)
                .memory(memory)
                .diskOptions(getDiskOptions())
                .networkOptions(getNetworkOptions())
                .diskIoOptions(getDiskIoOptions())
                .disk(disk)
                .network(network)
                .diskIo(diskIo)
                .build();
    }

    /**
     * 读取单个 current 快照
     *
     * @param key          Redis Key
     * @param type         目标类型
     * @param defaultValue 缓存为空时的兜底值
     * @return 缓存中的对象或兜底值
     * @param <T>          泛型类型
     */
    private <T> T readCurrent(String key, Class<T> type, T defaultValue) {
        T value = cacheReader.readObject(key, type);
        return value == null ? defaultValue : value;
    }

    /**
     * 归一化磁盘资源名
     * <p>
     * 兼容：
     * 1. 细接口沿用的 resourceName
     * 2. snapshot 接口专用的 diskResourceName
     */
    private String normalizeDiskResourceName(ServerMonitorQueryDTO queryDTO) {
        return normalizeResource(queryDTO == null ? null : firstNonBlank(queryDTO.getDiskResourceName(), queryDTO.getResourceName()));
    }

    /**
     * 归一化网络资源名
     */
    private String normalizeNetworkResourceName(ServerMonitorQueryDTO queryDTO) {
        return normalizeResource(queryDTO == null ? null : firstNonBlank(queryDTO.getNetworkResourceName(), queryDTO.getResourceName()));
    }

    /**
     * 归一化磁盘 IO 资源名
     */
    private String normalizeDiskIoResourceName(ServerMonitorQueryDTO queryDTO) {
        return normalizeResource(queryDTO == null ? null : firstNonBlank(queryDTO.getDiskIoResourceName(), queryDTO.getResourceName()));
    }

    /**
     * 将空资源统一折叠为 all
     */
    private String normalizeResource(String resourceName) {
        if (resourceName == null || resourceName.isBlank()) {
            return MonitorConstant.RESOURCE_ALL;
        }
        return resourceName;
    }

    /**
     * 归一化趋势点数量，防止前端传入非法值
     */
    private int normalizeLimit(ServerMonitorQueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getLimit() == null) {
            return MonitorConstant.DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(MonitorConstant.MAX_LIMIT, queryDTO.getLimit()));
    }

    /**
     * 容器模式下过滤明显不适合作为磁盘资源展示的挂载点
     *
     * @param options 原始选项列表
     * @return 过滤后的选项列表
     */
    private List<OptionVO> filterDiskOptions(List<OptionVO> options) {
        if (!environmentUtil.isContainerMode() || options.isEmpty()) {
            return options;
        }
        List<OptionVO> filtered = new java.util.ArrayList<>();
        for (OptionVO option : options) {
            if (option == null || option.getValue() == null) {
                continue;
            }
            if (MonitorConstant.RESOURCE_ALL.equals(option.getValue()) || !environmentUtil.isContainerNoiseMount(option.getValue())) {
                filtered.add(option);
            }
        }
        return filtered;
    }

    /**
     * 返回第一个非空白字符串
     */
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
