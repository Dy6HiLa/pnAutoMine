package ru.privatenull.pnautomine.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.privatenull.pnautomine.PnAutoMinePlugin;
import ru.privatenull.pnautomine.mine.Mine;
import ru.privatenull.pnautomine.mine.MineType;
import ru.privatenull.pnlibrary.text.ColorUtil;

import java.util.Comparator;
import java.util.Locale;

/**
 * PlaceholderAPI expansion for pnAutoMine.
 *
 * Placeholders:
 *   %pnautomine_<mine_id>_name%           - Display name of the mine
 *   %pnautomine_<mine_id>_type%           - Type ID
 *   %pnautomine_<mine_id>_type_display%   - Type display name (colored)
 *   %pnautomine_<mine_id>_blocks_total%   - Total blocks
 *   %pnautomine_<mine_id>_blocks_remaining% - Remaining blocks
 *   %pnautomine_<mine_id>_blocks_mined%   - Mined blocks
 *   %pnautomine_<mine_id>_percentage%     - Percentage remaining
 *   %pnautomine_<mine_id>_percentage_mined% - Percentage mined
 *   %pnautomine_<mine_id>_reset_time%     - Formatted time until reset
 *   %pnautomine_<mine_id>_reset_seconds%  - Seconds until reset (raw)
 *   %pnautomine_<mine_id>_reset_interval% - Reset interval in seconds
 *   %pnautomine_current_<value>%           - Value for the mine containing the player
 *   %pnautomine_current_exists%            - Whether the player is inside a mine
 *   %pnautomine_mine_count%                - Number of loaded mines
 */
public final class MinePlaceholderExpansion extends PlaceholderExpansion {

    private final PnAutoMinePlugin plugin;

    public MinePlaceholderExpansion(PnAutoMinePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pnautomine";
    }

    @Override
    public @NotNull String getAuthor() {
        return "privatenull";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("mine_count")) {
            return String.valueOf(plugin.getMineManager().getAllMines().size());
        }

        if (params.regionMatches(true, 0, "current_", 0, "current_".length())) {
            String placeholder = params.substring("current_".length()).toLowerCase(Locale.ROOT);
            Mine currentMine = player == null ? null : plugin.getMineManager().getMineAt(player.getLocation());
            if (placeholder.equals("exists")) {
                return String.valueOf(currentMine != null);
            }
            return currentMine == null ? "" : resolvePlaceholder(currentMine, placeholder);
        }

        // Mine IDs may contain underscores and one ID may prefix another.
        // Matching the longest ID first keeps placeholders deterministic.
        Mine mine = plugin.getMineManager().getAllMines().stream()
                .filter(candidate -> params.startsWith(candidate.getId() + "_"))
                .max(Comparator.comparingInt(candidate -> candidate.getId().length()))
                .orElse(null);
        if (mine == null) return null;

        String placeholder = params.substring(mine.getId().length() + 1).toLowerCase(Locale.ROOT);
        return resolvePlaceholder(mine, placeholder);
    }

    private String resolvePlaceholder(Mine mine, String placeholder) {
        return switch (placeholder) {
            case "id" -> mine.getId();
            case "name" -> mine.getDisplayName() != null ? ColorUtil.colorize(mine.getDisplayName()) : mine.getId();
            case "type" -> mine.getTypeName();
            case "type_display" -> {
                MineType type = plugin.getMineTypes().getType(mine.getTypeName());
                yield type != null ? ColorUtil.colorize(type.getDisplayName()) : mine.getTypeName();
            }
            case "blocks_total" -> String.valueOf(mine.getTotalBlocks());
            case "blocks_remaining" -> String.valueOf(mine.getRemainingBlocks());
            case "blocks_mined" -> String.valueOf(mine.getMinedBlocks());
            case "percentage" -> String.format(Locale.ROOT, "%.1f", mine.getPercentageRemaining());
            case "percentage_mined" -> String.format(Locale.ROOT, "%.1f", mine.getPercentageMined());
            case "reset_time" -> mine.getFormattedTimeUntilReset();
            case "reset_seconds" -> String.valueOf(mine.getSecondsUntilReset());
            case "reset_interval" -> String.valueOf(mine.getResetInterval());
            case "world" -> mine.getWorldName();
            default -> null;
        };
    }
}
