package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.HardwareDevice;
import com.manacommunity.safety.domain.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HardwareDeviceRepository extends JpaRepository<HardwareDevice, Long> {
    List<HardwareDevice> findByCommunityId(Long communityId);
    Optional<HardwareDevice> findByDeviceCode(String deviceCode);
    List<HardwareDevice> findByCommunityIdAndDeviceType(Long communityId, DeviceType deviceType);
}
