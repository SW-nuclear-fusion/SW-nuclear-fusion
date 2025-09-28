package com.example.swnuclearfusionwas.domain.plant.api;

import com.example.swnuclearfusionwas.domain.catalog.model.ItemType;
import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.home.dto.PlantDto;
import com.example.swnuclearfusionwas.domain.home.dto.SelectSeedReq;
import com.example.swnuclearfusionwas.domain.home.dto.UseItemReq;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.plant.service.PlantService;
import com.example.swnuclearfusionwas.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plant")
@RequiredArgsConstructor
public class PlantApi {

    private final UserQueryService users;
    private final PlantService plants;

    private UserEntity me(Authentication auth){
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        return users.getByUsernameOrThrow(auth.getName());
    }

    /** 씨앗 선택 → 활성 식물 생성 */
    @PostMapping("/select")
    public PlantDto select(Authentication auth, @RequestBody SelectSeedReq req){
        var u = me(auth);
        var p = plants.selectSeed(u, SeedType.valueOf(req.seedType()));
        return PlantDto.of(p);
    }

    /** 아이템 사용 → 성장 */
    @PostMapping("/use-item")
    public PlantDto useItem(Authentication auth, @RequestBody UseItemReq req){
        var u = me(auth);
        var p = plants.applyItem(u, req.plantId(),
                ItemType.valueOf(req.itemType()), req.quantity(), req.gainedPoints());
        return PlantDto.of(p);
    }

    /** 내 활성 식물 상태 */
    @GetMapping("/status")
    public PlantDto status(Authentication auth){
        var u = me(auth);
        return PlantDto.of(plants.getActiveOrNull(u));
    }
}