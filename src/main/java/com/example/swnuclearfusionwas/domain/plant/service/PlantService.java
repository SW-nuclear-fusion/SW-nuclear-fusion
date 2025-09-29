package com.example.swnuclearfusionwas.domain.plant.service;

import com.example.swnuclearfusionwas.domain.catalog.model.ItemType;
import com.example.swnuclearfusionwas.domain.catalog.model.PlantState;
import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.inventory.service.InventoryService;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.plant.entity.Plant;
import com.example.swnuclearfusionwas.domain.plant.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlantService {

    private final PlantRepository plantRepo;
    private final InventoryService inventory;

    public static final int MAX_LEVEL = 10;

    @Transactional(readOnly = true)
    public Plant getActiveOrNull(UserEntity u) {
        return plantRepo.findFirstByOwnerAndState(u, PlantState.ACTIVE).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean canSelectNext(UserEntity u) {
        return getActiveOrNull(u) == null && inventory.totalSeedCount(u) > 0;
    }

    @Transactional
    public Plant selectSeed(UserEntity u, SeedType type) {
        if (getActiveOrNull(u) != null)
            throw new IllegalStateException("이미 활성 식물이 있습니다.");
        inventory.consumeSeed(u, type, 1);
        var p = new Plant();
        p.setOwner(u);
        p.setSeedType(type);
        p.setNickname("새싹");
        return plantRepo.save(p);
    }

    /** 아이템 사용 → EXP 증가 → 레벨업/완료 */
    @Transactional
    public Plant applyItem(UserEntity u, Long plantId, ItemType item, int qty, int gainedPoints) {
        var p = plantRepo.findById(plantId).orElseThrow();
        if (!p.getOwner().getId().equals(u.getId())) throw new IllegalStateException("권한 없음");
        if (p.getState() != PlantState.ACTIVE) throw new IllegalStateException("활성 식물이 아님");

        inventory.consumeItem(u, item, qty);

        int expGain = switch (item) {
            case WATERING_CAN -> 15 * qty;
            case FERTILIZER  -> 30 * qty;
            case NUTRIENT    -> 20 * qty;
            case POT         -> 10 * qty;
        };

        int exp = p.getExp() + expGain;
        int level = p.getLevel();
        while (exp >= 100 && level < MAX_LEVEL) { exp -= 100; level++; }
        p.setLevel(level);
        p.setExp(Math.min(exp, 99));

        boolean finishedByPoint = gainedPoints >= requiredPointsForLevel(level);
        if (p.getLevel() >= MAX_LEVEL || finishedByPoint) {
            p.setState(PlantState.FINISHED);
            p.setExp(0);
        }
        return p;
    }

    private int requiredPointsForLevel(int level) {
        return level * 50; // 예시 정책
    }
}