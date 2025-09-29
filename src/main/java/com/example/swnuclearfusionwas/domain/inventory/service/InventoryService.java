package com.example.swnuclearfusionwas.domain.inventory.service;

import com.example.swnuclearfusionwas.domain.catalog.model.ItemType;
import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.inventory.entity.ItemInventory;
import com.example.swnuclearfusionwas.domain.inventory.entity.SeedInventory;
import com.example.swnuclearfusionwas.domain.inventory.repository.ItemInventoryRepository;
import com.example.swnuclearfusionwas.domain.inventory.repository.SeedInventoryRepository;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SeedInventoryRepository seedRepo;
    private final ItemInventoryRepository itemRepo;

    @Transactional
    public void addSeed(UserEntity u, SeedType type, int qty){
        var row = seedRepo.findByUserAndType(u, type).orElseGet(() -> {
            var s = new SeedInventory(); s.setUser(u); s.setType(type); return s;
        });
        row.setQuantity(row.getQuantity() + qty);
        seedRepo.save(row);
    }

    @Transactional
    public void consumeSeed(UserEntity u, SeedType type, int qty){
        var row = seedRepo.findByUserAndType(u, type)
                .orElseThrow(() -> new IllegalStateException("seed not found"));
        if (row.getQuantity() < qty) throw new IllegalStateException("insufficient seed");
        row.setQuantity(row.getQuantity() - qty);
        seedRepo.save(row);
    }

    @Transactional
    public void addItem(UserEntity u, ItemType type, int qty){
        var row = itemRepo.findByUserAndType(u, type).orElseGet(() -> {
            var i = new ItemInventory(); i.setUser(u); i.setType(type); return i;
        });
        row.setQuantity(row.getQuantity() + qty);
        itemRepo.save(row);
    }

    @Transactional
    public void consumeItem(UserEntity u, ItemType type, int qty){
        var row = itemRepo.findByUserAndType(u, type)
                .orElseThrow(() -> new IllegalStateException("item not found"));
        if (row.getQuantity() < qty) throw new IllegalStateException("insufficient item");
        row.setQuantity(row.getQuantity() - qty);
        itemRepo.save(row);
    }

    @Transactional(readOnly = true)
    public List<SeedInventory> listSeeds(UserEntity u){ return seedRepo.findByUser(u); }

    @Transactional(readOnly = true)
    public List<ItemInventory> listItems(UserEntity u){ return itemRepo.findByUser(u); }

    @Transactional(readOnly = true)
    public int totalSeedCount(UserEntity u){
        return listSeeds(u).stream().mapToInt(SeedInventory::getQuantity).sum();
    }
}