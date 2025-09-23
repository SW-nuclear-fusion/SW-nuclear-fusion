package com.example.swnuclearfusionwas.domain.meds.repository;

import com.example.swnuclearfusionwas.domain.meds.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByUserId(Long userId);

    Optional<PushSubscription> findByUserIdAndEndpoint(Long userId, String endpoint);
}
