package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.IncidentUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentUpdateRepository extends JpaRepository<IncidentUpdate, Long> {
    List<IncidentUpdate> findByIncidentIdOrderByTimestampAsc(Long incidentId);
}
