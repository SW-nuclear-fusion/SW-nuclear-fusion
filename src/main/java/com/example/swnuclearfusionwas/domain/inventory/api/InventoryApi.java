package com.example.swnuclearfusionwas.domain.inventory.api;

import com.example.swnuclearfusionwas.domain.home.dto.ItemRow;
import com.example.swnuclearfusionwas.domain.home.dto.SeedRow;
import com.example.swnuclearfusionwas.domain.inventory.service.InventoryService;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryApi {

    private final UserQueryService users;
    private final InventoryService inventory;

    private UserEntity me(Authentication auth){
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        return users.getByUsernameOrThrow(auth.getName());
    }

    @GetMapping("/seeds")
    public java.util.List<SeedRow> seeds(Authentication auth){
        var u = me(auth);
        return inventory.listSeeds(u).stream().map(SeedRow::from).toList();
    }

    @GetMapping("/items")
    public java.util.List<ItemRow> items(Authentication auth){
        var u = me(auth);
        return inventory.listItems(u).stream().map(ItemRow::from).toList();
    }
}