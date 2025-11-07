package com.example.back.mypage.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ViewCountResponse {
    private Long caregiverId;
    private Long seniorId;
    private long count;
}

