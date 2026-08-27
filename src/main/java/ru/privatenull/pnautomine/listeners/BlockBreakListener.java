package ru.privatenull.pnautomine.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.privatenull.pnautomine.PnAutoMinePlugin;
import ru.privatenull.pnautomine.mine.Mine;

/**
 * Tracks block breaks inside mines to update mined block counts.
 */
public final class BlockBreakListener implements Listener {

    private final PnAutoMinePlugin plugin;

    public BlockBreakListener(PnAutoMinePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Mine mine = plugin.getMineManager().getMineAt(event.getBlock().getLocation());
        if (mine != null) {
            mine.recordMinedBlock(event.getPlayer().getUniqueId(), event.getBlock().getType());
        }
    }
}
