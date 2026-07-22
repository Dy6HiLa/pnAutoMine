package ru.privatenull.pnautomine.mine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a configured auto-mine with its region, type, and runtime state.
 */
public final class Mine {

    private final String id;
    private String typeName;
    private String displayName;
    private String worldName;
    private MineRegion region;
    private Location spawnLocation;
    private Location hologramLocation;
    private int resetInterval;
    private boolean broadcastReset;
    private boolean teleportOnReset;
    private String resetSound;

    // Runtime state
    private int totalBlocks;
    private int minedBlocks;
    private long lastResetTime;

    public Mine(String id) {
        this.id = id;
        this.lastResetTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public MineRegion getRegion() {
        return region;
    }

    public void setRegion(MineRegion region) {
        this.region = region;
        if (region != null) {
            this.totalBlocks = region.blockCount();
        }
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getHologramLocation() {
        return hologramLocation;
    }

    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    public int getResetInterval() {
        return resetInterval;
    }

    public void setResetInterval(int resetInterval) {
        this.resetInterval = resetInterval;
    }

    public boolean isBroadcastReset() {
        return broadcastReset;
    }

    public void setBroadcastReset(boolean broadcastReset) {
        this.broadcastReset = broadcastReset;
    }

    public boolean isTeleportOnReset() {
        return teleportOnReset;
    }

    public void setTeleportOnReset(boolean teleportOnReset) {
        this.teleportOnReset = teleportOnReset;
    }

    public String getResetSound() {
        return resetSound;
    }

    public void setResetSound(String resetSound) {
        this.resetSound = resetSound;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public int getMinedBlocks() {
        return minedBlocks;
    }

    public void setMinedBlocks(int minedBlocks) {
        this.minedBlocks = minedBlocks;
    }

    public void incrementMinedBlocks() {
        this.minedBlocks++;
    }

    public int getRemainingBlocks() {
        return Math.max(0, totalBlocks - minedBlocks);
    }

    public double getPercentageRemaining() {
        if (totalBlocks == 0) return 0.0;
        return (getRemainingBlocks() * 100.0) / totalBlocks;
    }

    public double getPercentageMined() {
        if (totalBlocks == 0) return 100.0;
        return (minedBlocks * 100.0) / totalBlocks;
    }

    public long getLastResetTime() {
        return lastResetTime;
    }

    public void setLastResetTime(long lastResetTime) {
        this.lastResetTime = lastResetTime;
    }

    public long getNextResetTime() {
        return lastResetTime + (resetInterval * 1000L);
    }

    public long getSecondsUntilReset() {
        long remaining = (getNextResetTime() - System.currentTimeMillis()) / 1000L;
        return Math.max(0, remaining);
    }

    public String getFormattedTimeUntilReset() {
        long seconds = getSecondsUntilReset();
        if (seconds <= 0) return "Скоро";
        long minutes = seconds / 60;
        long secs = seconds % 60;
        if (minutes > 0) {
            return minutes + "м " + secs + "с";
        }
        return secs + "с";
    }

    /** Returns the center-top point of the mine region. */
    public Location getCenterTop() {
        if (region == null || worldName == null) return null;
        World world = getWorld();
        if (world == null) return null;
        return region.centerTop(world);
    }
}
