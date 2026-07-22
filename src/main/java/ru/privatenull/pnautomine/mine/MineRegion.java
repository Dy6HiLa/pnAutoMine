package ru.privatenull.pnautomine.mine;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Stores the serialized region as a list of individual block positions.
 * This supports ANY shape from WorldEdit (polygonal, sphere, cuboid, etc.).
 */
public final class MineRegion {

    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final List<int[]> blocks;

    public MineRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, List<int[]> blocks) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.blocks = blocks;
    }

    /** Creates a region from a bounding box (cuboid). */
    public static MineRegion cuboid(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        List<int[]> blocks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new int[]{x, y, z});
                }
            }
        }
        return new MineRegion(minX, minY, minZ, maxX, maxY, maxZ, blocks);
    }

    /** Creates a region from an explicit list of block positions. */
    public static MineRegion fromBlocks(List<int[]> blocks) {
        if (blocks.isEmpty()) {
            return new MineRegion(0, 0, 0, 0, 0, 0, blocks);
        }
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (int[] block : blocks) {
            minX = Math.min(minX, block[0]);
            minY = Math.min(minY, block[1]);
            minZ = Math.min(minZ, block[2]);
            maxX = Math.max(maxX, block[0]);
            maxY = Math.max(maxY, block[1]);
            maxZ = Math.max(maxZ, block[2]);
        }
        return new MineRegion(minX, minY, minZ, maxX, maxY, maxZ, blocks);
    }

    public int blockCount() {
        return blocks.size();
    }

    public List<int[]> getBlocks() {
        return blocks;
    }

    public void forEach(BiConsumer<Integer, int[]> consumer) {
        for (int i = 0; i < blocks.size(); i++) {
            consumer.accept(i, blocks.get(i));
        }
    }

    public boolean contains(int x, int y, int z) {
        if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ) {
            return false;
        }
        for (int[] block : blocks) {
            if (block[0] == x && block[1] == y && block[2] == z) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPlayer(Location location) {
        int px = location.getBlockX();
        int py = location.getBlockY();
        int pz = location.getBlockZ();
        // Use bounding box for player containment (sufficient for teleport)
        return px >= minX && px <= maxX && py >= minY && py <= maxY && pz >= minZ && pz <= maxZ;
    }

    public Location centerTop(World world) {
        double cx = (minX + maxX) / 2.0 + 0.5;
        double cz = (minZ + maxZ) / 2.0 + 0.5;
        return new Location(world, cx, maxY + 1.5, cz);
    }

    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
}
