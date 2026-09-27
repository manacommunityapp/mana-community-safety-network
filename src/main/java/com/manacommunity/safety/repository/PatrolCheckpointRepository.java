package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.PatrolCheckpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrolCheckpointRepository extends JpaRepository<PatrolCheckpoint, Long> {
    List<PatrolCheckpoint> findByRouteIdOrderBySequenceOrderAsc(Long routeId);
}
