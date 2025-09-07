package com.example.swnuclearfusionwas.domain.mypage.repository;

import com.example.swnuclearfusionwas.domain.mypage.entity.LinkReq;
import com.example.swnuclearfusionwas.domain.mypage.model.LinkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkReqRepository extends JpaRepository<LinkReq, Long> {

    boolean existsByInitiatorUserIdAndRecipientUserIdAndStatus(
            Long initiatorUserId, Long recipientUserId, LinkStatus status
    );
}