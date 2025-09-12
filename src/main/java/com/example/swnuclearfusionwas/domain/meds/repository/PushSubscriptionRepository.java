package com.example.swnuclearfusionwas.domain.meds.repository;

import com.example.swnuclearfusionwas.domain.meds.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByUserId(Long userId);
}
