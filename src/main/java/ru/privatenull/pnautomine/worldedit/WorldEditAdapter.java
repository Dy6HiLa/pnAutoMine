package ru.privatenull.pnautomine.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.entity.Player;
import ru.privatenull.pnautomine.mine.MineRegion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adapter for WorldEdit that extracts ANY region shape (cuboid, poly, sphere, etc.)
 * as individual block positions. This allows pnAutoMine to support all WorldEdit shapes.
 */
public final class WorldEditAdapter {

    private WorldEditAdapter() {
    }

    /**
     * Gets the player's current WorldEdit selection as a MineRegion.
     * Supports ALL WorldEdit region types (cuboid, polygon, sphere, cylinder, etc.)
     * by iterating every block position inside the selection.
     *
     * @param player the player with an active WorldEdit selection
     * @return the MineRegion, or null if no selection exists
     */
    public static MineRegion getSelection(Player player) {
        try {
            SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
            com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
            Region region = sessionManager.get(wePlayer).getSelection(wePlayer.getWorld());

            if (region == null) return null;

            List<int[]> blocks = new ArrayList<>();
            Iterator<BlockVector3> iterator = region.iterator();
            while (iterator.hasNext()) {
                BlockVector3 vec = iterator.next();
                blocks.add(new int[]{vec.x(), vec.y(), vec.z()});
            }

            if (blocks.isEmpty()) return null;

            return MineRegion.fromBlocks(blocks);
        } catch (IncompleteRegionException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
