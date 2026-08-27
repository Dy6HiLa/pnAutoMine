package ru.privatenull.pnautomine.mine;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MineTest {

    @Test
    void tracksMineAndPlayerMaterialsForCurrentResetCycle() {
        Mine mine = new Mine("test");
        UUID firstPlayer = UUID.randomUUID();
        UUID secondPlayer = UUID.randomUUID();

        mine.recordMinedBlock(firstPlayer, Material.IRON_ORE);
        mine.recordMinedBlock(firstPlayer, Material.DEEPSLATE_IRON_ORE);
        mine.recordMinedBlock(firstPlayer, Material.STONE);
        mine.recordMinedBlock(secondPlayer, Material.IRON_ORE);

        assertEquals(4, mine.getMinedBlocks());
        assertEquals(3, mine.getMinedBlocks(List.of(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)));
        assertEquals(3, mine.getPlayerMinedBlocks(firstPlayer));
        assertEquals(2, mine.getPlayerMinedBlocks(firstPlayer,
                List.of(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)));
        assertEquals(1, mine.getPlayerMinedBlocks(secondPlayer));
    }

    @Test
    void resetClearsGlobalAndPerPlayerStatistics() {
        Mine mine = new Mine("test");
        UUID playerId = UUID.randomUUID();
        mine.recordMinedBlock(playerId, Material.DIAMOND_ORE);

        mine.resetMiningStats();

        assertEquals(0, mine.getMinedBlocks());
        assertEquals(0, mine.getPlayerMinedBlocks(playerId));
        assertTrue(mine.getPlayerMinedBlocksSnapshot(playerId).isEmpty());
    }
}
