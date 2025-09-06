package com.example.swnuclearfusionwas.domain.home.dto;

import com.example.swnuclearfusionwas.domain.plant.entity.Plant;
import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import java.util.List;

public record HomeView(
        Long userId, String username,
        int level, int points, int seedsOwned,
        List<PlantCard> plants,
        String nextReward
) {
    public static HomeView of(UserProfile prof, String username, List<Plant> plants){
        return new HomeView(
                prof.getUser().getId(),
                username,
                prof.getLevel(),
                prof.getPoints(),
                prof.getSeedsOwned(),
                plants.stream().map(p -> new PlantCard(p.getId(), p.getNickname(), p.getLevel(), p.getExp())).toList(),
                nextRewardOf(prof.getLevel())
        );
    }
    public static String nextRewardOf(int level){
        return (level>=5) ? "최대 레벨 도달" :
                switch (level+1){ case 2->"씨앗"; case 3->"새싹"; case 4->"이파리";
                    case 5->"줄기"; case 6->"꽃"; default->"보상 준비중"; };
    }
    public record PlantCard(Long id, String nickname, int level, int exp){}
}
