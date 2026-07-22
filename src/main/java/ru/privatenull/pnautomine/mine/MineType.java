package ru.privatenull.pnautomine.mine;

import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a mine type with block distribution weights.
 */
public final class MineType {

    private final String id;
    private final String displayName;
    private final Map<Material, Integer> blocks;
    private final int totalWeight;

    public MineType(String id, String displayName, Map<Material, Integer> blocks) {
        this.id = id;
        this.displayName = displayName;
        this.blocks = new LinkedHashMap<>(blocks);
        this.totalWeight = blocks.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Map<Material, Integer> getBlocks() {
        return blocks;
    }

    /**
     * Picks a random block material based on configured weights.
     */
    public Material randomBlock() {
        if (blocks.isEmpty()) return Material.STONE;
        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (var entry : blocks.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }
        return blocks.keySet().iterator().next();
    }
}
