package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.PatrolCheckpoint;
import com.manacommunity.safety.domain.entities.PatrolLog;
import com.manacommunity.safety.domain.entities.PatrolRoute;
import com.manacommunity.safety.domain.enums.PatrolStatus;
import com.manacommunity.safety.dto.request.CreatePatrolRouteRequest;
import com.manacommunity.safety.dto.request.ScanCheckpointRequest;
import com.manacommunity.safety.repository.PatrolCheckpointRepository;
import com.manacommunity.safety.repository.PatrolLogRepository;
import com.manacommunity.safety.repository.PatrolRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatrolService {

    private final PatrolRouteRepository patrolRouteRepository;
    private final PatrolCheckpointRepository patrolCheckpointRepository;
    private final PatrolLogRepository patrolLogRepository;

    @Transactional
    public PatrolRoute createRoute(CreatePatrolRouteRequest req) {
        PatrolRoute route = PatrolRoute.builder()
                .communityId(req.getCommunityId())
                .routeName(req.getRouteName())
                .description(req.getDescription())
                .estimatedDurationMinutes(req.getEstimatedDurationMinutes() != null ? req.getEstimatedDurationMinutes() : 30)
                .checkpointCount(req.getCheckpoints() != null ? req.getCheckpoints().size() : 0)
                .isActive(true)
                .build();

        route = patrolRouteRepository.save(route);

        if (req.getCheckpoints() != null) {
            for (CreatePatrolRouteRequest.CheckpointDto cp : req.getCheckpoints()) {
                PatrolCheckpoint checkpoint = PatrolCheckpoint.builder()
                        .routeId(route.getId())
                        .checkpointName(cp.getCheckpointName())
                        .location(cp.getLocation())
                        .nfcTagId(cp.getNfcTagId())
                        .qrCode(cp.getQrCode())
                        .sequenceOrder(cp.getSequenceOrder())
                        .build();
                patrolCheckpointRepository.save(checkpoint);
            }
        }
        return route;
    }

    @Transactional
    public PatrolLog startPatrol(Long communityId, Long routeId, Long guardUserId, String guardName) {
        PatrolRoute route = patrolRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("PatrolRoute", "id", routeId));

        PatrolLog log = PatrolLog.builder()
                .communityId(communityId)
                .routeId(routeId)
                .guardUserId(guardUserId)
                .guardName(guardName)
                .startTime(LocalDateTime.now())
                .status(PatrolStatus.IN_PROGRESS)
                .scannedCheckpoints(0)
                .totalCheckpoints(route.getCheckpointCount())
                .build();

        return patrolLogRepository.save(log);
    }

    @Transactional
    public PatrolLog scanCheckpoint(ScanCheckpointRequest req) {
        PatrolLog log = patrolLogRepository.findById(req.getPatrolLogId())
                .orElseThrow(() -> new ResourceNotFoundException("PatrolLog", "id", req.getPatrolLogId()));

        log.setScannedCheckpoints(log.getScannedCheckpoints() + 1);
        if (log.getScannedCheckpoints() >= log.getTotalCheckpoints()) {
            log.setStatus(PatrolStatus.COMPLETED);
            log.setEndTime(LocalDateTime.now());
        }
        return patrolLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<PatrolRoute> getRoutes(Long communityId) {
        return patrolRouteRepository.findByCommunityIdAndIsActiveTrue(communityId);
    }
}
