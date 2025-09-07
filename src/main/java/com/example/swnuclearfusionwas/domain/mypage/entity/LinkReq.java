package com.example.swnuclearfusionwas.domain.mypage.entity;

import com.example.swnuclearfusionwas.domain.mypage.model.LinkStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "link_requests")
public class LinkReq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long initiatorUserId;

    @Column(nullable = false)
    private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkStatus status;

}