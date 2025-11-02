package com.example.back.mission.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardDto {
    private String type; // "water" 또는 "affection"
    private int amount;
}