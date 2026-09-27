package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.IncidentUpdate;
import com.manacommunity.safety.domain.entities.SafetyIncident;
import com.manacommunity.safety.domain.enums.IncidentStatus;
import com.manacommunity.safety.dto.request.CreateSafetyIncidentRequest;
import com.manacommunity.safety.dto.request.UpdateIncidentStatusRequest;
import com.manacommunity.safety.repository.IncidentUpdateRepository;
import com.manacommunity.safety.repository.SafetyIncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final SafetyIncidentRepository incidentRepository;
    private final IncidentUpdateRepository updateRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public SafetyIncident reportIncident(Long userId, String userName, CreateSafetyIncidentRequest req) {
        String incNumber = "INC-" + System.currentTimeMillis();

        SafetyIncident incident = SafetyIncident.builder()
                .communityId(req.getCommunityId())
                .incidentNumber(incNumber)
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .severity(req.getSeverity())
                .status(IncidentStatus.REPORTED)
                .location(req.getLocation())
                .tower(req.getTower())
                .flatNumber(req.getFlatNumber())
                .reportedByUserId(userId)
                .reportedByName(userName)
                .evidencePhotos(req.getEvidencePhotos())
                .build();

        SafetyIncident saved = incidentRepository.save(incident);
        messagingTemplate.convertAndSend("/topic/community/" + req.getCommunityId() + "/incidents", saved);
        return saved;
    }

    @Transactional
    public SafetyIncident updateIncidentStatus(Long incidentId, String authorName, String authorRole, UpdateIncidentStatusRequest req) {
        SafetyIncident inc = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("SafetyIncident", "id", incidentId));

        inc.setStatus(req.getStatus());
        if (req.getStatus() == IncidentStatus.RESOLVED || req.getStatus() == IncidentStatus.CLOSED) {
            inc.setResolvedAt(LocalDateTime.now());
            if (req.getResolutionNotes() != null) {
                inc.setResolutionNotes(req.getResolutionNotes());
            }
        }

        if (req.getNote() != null && !req.getNote().isBlank()) {
            IncidentUpdate update = IncidentUpdate.builder()
                    .incidentId(incidentId)
                    .authorName(authorName)
                    .authorRole(authorRole)
                    .note(req.getNote())
                    .build();
            updateRepository.save(update);
        }

        return incidentRepository.save(inc);
    }

    @Transactional(readOnly = true)
    public List<SafetyIncident> getIncidents(Long communityId) {
        return incidentRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }

    @Transactional(readOnly = true)
    public List<IncidentUpdate> getIncidentUpdates(Long incidentId) {
        return updateRepository.findByIncidentIdOrderByTimestampAsc(incidentId);
    }
}
