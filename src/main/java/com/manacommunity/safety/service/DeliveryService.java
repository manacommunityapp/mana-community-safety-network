package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.DeliveryLog;
import com.manacommunity.safety.domain.enums.DeliveryDropLocation;
import com.manacommunity.safety.domain.enums.DeliveryStatus;
import com.manacommunity.safety.dto.request.LogDeliveryRequest;
import com.manacommunity.safety.repository.DeliveryLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryLogRepository deliveryLogRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public DeliveryLog logDeliveryArrival(LogDeliveryRequest req) {
        String otp = String.format("%04d", RANDOM.nextInt(10000));
        DeliveryStatus status = req.getDropLocation() == DeliveryDropLocation.DOORSTEP 
                ? DeliveryStatus.OUT_FOR_DOOR_DELIVERY 
                : DeliveryStatus.LEFT_AT_GATE;

        DeliveryLog log = DeliveryLog.builder()
                .communityId(req.getCommunityId())
                .flatNumber(req.getFlatNumber())
                .tower(req.getTower())
                .courierCompany(req.getCourierCompany())
                .deliveryAgentName(req.getDeliveryAgentName())
                .deliveryAgentPhone(req.getDeliveryAgentPhone())
                .packageCount(req.getPackageCount() != null ? req.getPackageCount() : 1)
                .dropLocation(req.getDropLocation())
                .status(status)
                .lockerSlot(req.getLockerSlot())
                .collectionOtp(otp)
                .packagePhotoUrl(req.getPackagePhotoUrl())
                .gateArrivedAt(LocalDateTime.now())
                .build();

        DeliveryLog saved = deliveryLogRepository.save(log);
        messagingTemplate.convertAndSend("/topic/flat/" + req.getFlatNumber() + "/deliveries", saved);
        return saved;
    }

    @Transactional
    public DeliveryLog collectDelivery(Long deliveryId, Long residentId, String otp) {
        DeliveryLog log = deliveryLogRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryLog", "id", deliveryId));

        if (!log.getCollectionOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid collection OTP");
        }

        log.setStatus(DeliveryStatus.COLLECTED_BY_RESIDENT);
        log.setCollectedAt(LocalDateTime.now());
        log.setCollectedByUserId(residentId);

        return deliveryLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<DeliveryLog> getFlatDeliveries(Long communityId, String flatNumber) {
        return deliveryLogRepository.findByCommunityIdAndFlatNumberOrderByCreatedAtDesc(communityId, flatNumber);
    }
}
