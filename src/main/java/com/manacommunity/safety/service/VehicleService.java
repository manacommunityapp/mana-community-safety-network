package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.Vehicle;
import com.manacommunity.safety.domain.entities.VehicleAccessLog;
import com.manacommunity.safety.dto.request.RegisterVehicleRequest;
import com.manacommunity.safety.repository.VehicleAccessLogRepository;
import com.manacommunity.safety.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleAccessLogRepository vehicleAccessLogRepository;

    @Transactional
    public Vehicle registerVehicle(RegisterVehicleRequest req) {
        Vehicle vehicle = Vehicle.builder()
                .communityId(req.getCommunityId())
                .residentId(req.getResidentId())
                .licensePlate(req.getLicensePlate().toUpperCase().replaceAll("\\s+", ""))
                .rfidTagNumber(req.getRfidTagNumber())
                .vehicleType(req.getVehicleType())
                .accessType(req.getAccessType())
                .makeModel(req.getMakeModel())
                .color(req.getColor())
                .flatNumber(req.getFlatNumber())
                .tower(req.getTower())
                .parkingSlotNumber(req.getParkingSlotNumber())
                .isEv(req.getIsEv() != null ? req.getIsEv() : false)
                .isActive(true)
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getResidentVehicles(Long communityId, Long residentId) {
        return vehicleRepository.findByCommunityIdAndResidentId(communityId, residentId);
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> findByLicensePlate(Long communityId, String licensePlate) {
        return vehicleRepository.findByCommunityIdAndLicensePlateIgnoreCase(
                communityId, licensePlate.toUpperCase().replaceAll("\\s+", ""));
    }

    @Transactional(readOnly = true)
    public List<VehicleAccessLog> getRecentVehicleLogs(Long communityId) {
        return vehicleAccessLogRepository.findByCommunityIdOrderByAccessTimeDesc(communityId);
    }
}
