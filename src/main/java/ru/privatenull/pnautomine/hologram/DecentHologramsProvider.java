package ru.privatenull.pnautomine.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class DecentHologramsProvider implements HologramProvider {

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    }

    @Override
    public void create(HologramSpec spec) {
        if (!isAvailable()) {
            throw new IllegalStateException("DecentHolograms API недоступен");
        }

        Hologram hologram = DHAPI.getHologram(spec.name());
        List<String> lines = colorLines(spec.lines());
        if (hologram != null) {
            if (!hologram.getLocation().equals(spec.location())) {
                DHAPI.moveHologram(hologram, spec.location());
            }
            DHAPI.setHologramLines(hologram, lines);
            hologram.updateAll(true);
            return;
        }

        hologram = DHAPI.createHologram(spec.name(), spec.location(), false, lines);
        hologram.setSaveToFile(false);
        hologram.updateAll();
    }

    @Override
    public void remove(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        DHAPI.removeHologram(name);
    }

    private static List<String> colorLines(List<String> lines) {
        List<String> out = new ArrayList<>(lines.size());
        for (String line : lines) {
            out.add(line == null ? "" : line.replace('&', '§'));
        }
        return out;
    }
}
