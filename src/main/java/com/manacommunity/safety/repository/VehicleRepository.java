package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCommunityIdAndResidentId(Long communityId, Long residentId);
    Optional<Vehicle> findByCommunityIdAndLicensePlateIgnoreCase(Long communityId, String licensePlate);
    Optional<Vehicle> findByCommunityIdAndRfidTagNumber(Long communityId, String rfidTagNumber);
    List<Vehicle> findByCommunityIdAndFlatNumber(Long communityId, String flatNumber);
}
