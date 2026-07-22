package ru.privatenull.pnautomine.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ru.privatenull.pnautomine.mine.MineType;

import java.util.*;

/**
 * Loads mine types from config.yml.
 */
public final class MineTypesConfig {

    private final JavaPlugin plugin;
    private final Map<String, MineType> types = new LinkedHashMap<>();

    public MineTypesConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        types.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("mine-types");
        if (section == null) {
            plugin.getLogger().warning("Секция mine-types не найдена в config.yml");
            return;
        }

        for (String typeId : section.getKeys(false)) {
            ConfigurationSection typeSection = section.getConfigurationSection(typeId);
            if (typeSection == null) continue;

            String displayName = typeSection.getString("display-name", "&7" + typeId);

            ConfigurationSection blocksSection = typeSection.getConfigurationSection("blocks");
            if (blocksSection == null) {
                plugin.getLogger().warning("Тип шахты '" + typeId + "' не содержит секцию blocks");
                continue;
            }

            Map<Material, Integer> blocks = new LinkedHashMap<>();
            for (String materialName : blocksSection.getKeys(false)) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    int weight = blocksSection.getInt(materialName, 1);
                    blocks.put(material, weight);
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Неизвестный материал '" + materialName
                            + "' в типе шахты '" + typeId + "'");
                }
            }

            if (!blocks.isEmpty()) {
                types.put(typeId, new MineType(typeId, displayName, blocks));
            }
        }

        plugin.getLogger().info("Загружено типов шахт: " + types.size());
    }

    public MineType getType(String id) {
        return types.get(id);
    }

    public Collection<MineType> getAllTypes() {
        return Collections.unmodifiableCollection(types.values());
    }

    public Set<String> getTypeIds() {
        return Collections.unmodifiableSet(types.keySet());
    }

    public boolean hasType(String id) {
        return types.containsKey(id);
    }
}
