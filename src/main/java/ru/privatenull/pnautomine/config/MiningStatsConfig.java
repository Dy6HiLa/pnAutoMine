package ru.privatenull.pnautomine.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Loads configurable material groups used by mining-stat placeholders. */
public final class MiningStatsConfig {

    private final JavaPlugin plugin;
    private volatile Map<String, Set<Material>> groups = Map.of();
    private volatile Map<Material, Double> values = Map.of();
    private volatile int salaryDecimals;

    public MiningStatsConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        Map<String, Set<Material>> loadedGroups = new LinkedHashMap<>();
        Map<Material, Double> loadedValues = new EnumMap<>(Material.class);
        int loadedSalaryDecimals = Math.max(0, Math.min(4,
                plugin.getConfig().getInt("mining-statistics.salary-decimals", 0)));

        ConfigurationSection section = plugin.getConfig()
                .getConfigurationSection("mining-statistics.groups");
        if (section == null) {
            plugin.getLogger().warning("Секция mining-statistics.groups не найдена в config.yml");
            groups = Map.of();
            values = Map.of();
            salaryDecimals = loadedSalaryDecimals;
            return;
        }

        for (String rawId : section.getKeys(false)) {
            ConfigurationSection groupSection = section.getConfigurationSection(rawId);
            if (groupSection == null) continue;

            String id = rawId.toLowerCase(Locale.ROOT);
            Set<Material> materials = new LinkedHashSet<>();
            for (String materialName : groupSection.getStringList("materials")) {
                Material material = Material.matchMaterial(materialName);
                if (material == null || !material.isBlock()) {
                    plugin.getLogger().warning("Неизвестный блок '" + materialName
                            + "' в mining-statistics.groups." + rawId);
                    continue;
                }
                materials.add(material);
            }

            if (materials.isEmpty()) {
                plugin.getLogger().warning("Группа статистики '" + rawId + "' не содержит блоков");
                continue;
            }

            loadedGroups.put(id, Collections.unmodifiableSet(materials));
            double value = Math.max(0.0, groupSection.getDouble("value-per-block", 0.0));
            for (Material material : materials) {
                loadedValues.putIfAbsent(material, value);
            }
        }

        groups = Collections.unmodifiableMap(loadedGroups);
        values = Collections.unmodifiableMap(loadedValues);
        salaryDecimals = loadedSalaryDecimals;
    }

    public Collection<Material> resolveMaterials(String groupOrMaterial) {
        if (groupOrMaterial == null || groupOrMaterial.isBlank()) return Set.of();

        String key = groupOrMaterial.toLowerCase(Locale.ROOT);
        Set<Material> group = groups.get(key);
        if (group != null) return group;

        Material material = Material.matchMaterial(groupOrMaterial);
        return material != null && material.isBlock() ? Set.of(material) : Set.of();
    }

    public double calculateEarnings(Map<Material, Integer> minedBlocks) {
        double result = 0.0;
        for (Map.Entry<Material, Integer> entry : minedBlocks.entrySet()) {
            result += entry.getValue() * values.getOrDefault(entry.getKey(), 0.0);
        }
        return result;
    }

    public String formatEarnings(double value) {
        return String.format(Locale.ROOT, "%." + salaryDecimals + "f", value);
    }
}
