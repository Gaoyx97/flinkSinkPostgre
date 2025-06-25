package org.example.map;

import com.alibaba.fastjson2.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.example.entity.DeviceStatus;
import org.example.entity.TransferPlan;
import org.example.entity.TransferPlanDto;

public class TransferPlanMap implements MapFunction<String, TransferPlan> {


    @Override
    public TransferPlan map(String jsonStr) throws Exception {

        TransferPlanDto dto = JSON.parseObject(jsonStr, TransferPlanDto.class);

        String workMode = switch (dto.workMode()) {
            case 1 -> "SS";
            case 2 -> "SH";
            case 3 -> "SCSH";
            default -> null;
        };
        String rxProtocol = switch (dto.rxProtocol()) {
            case 1 -> "FEP";
            case 2 -> "PDXP";
            default -> "其他";
        };
        String txProtocal = switch (dto.txProtocal()) {
            case 1 -> "FEP";
            case 2 -> "PDXP";
            default -> "其他";
        };
        Integer status = switch (dto.status()) {
            case 1 -> 2;
            case 2 -> 0;
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 0;
            default -> null;
        };
        Integer currentNode = 1;
        for (Integer code : dto.currentNode()) {
            if(code == 1){
                currentNode ++ ;
            }else{
                break;
            }
        }

        TransferPlan transferPlan = new TransferPlan()
                .sidName(dto.sidName())
                .planId(dto.planId())
                .circleNo(dto.circleNo())
                .taskCode(dto.taskCode())
                .deviceCode(dto.deviceCode())
                .transmissionNo(dto.transmissionNo())
                .workMode(workMode)
                .transmissionCenter(dto.transmissionCenter())
                .priorityLevel(dto.priorityLevel())
                .allocationRate(dto.allocationRate())
                .relaySatellite(dto.relaySatellite())
                .channelNo(dto.channelNo())
                .rxProtocol(rxProtocol)
                .txProtocal(txProtocal)
                .rxFrameCount(dto.rxFrameCount())
                .rxRate(dto.rxRate())
                .rxDataVolume(dto.rxDataVolume())
                .txFrameCount(dto.txFrameCount())
                .txRate(dto.txRate())
                .txDataVolume(dto.txDataVolume())
                .currentNode(currentNode)
                .status(status)
                .startTime(dto.startTime())
                .endTime(dto.endTime());
        if(transferPlan.priorityLevel() != 1){
            transferPlan.result("待处理");
        }else{
            transferPlan.result("");
        }

        return transferPlan;
    }

}
