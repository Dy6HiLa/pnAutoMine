package ru.privatenull.pnautomine.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;

public final class FancyHologramsProvider implements HologramProvider {

    private final HologramManager manager;

    public FancyHologramsProvider() {
        this.manager = FancyHologramsPlugin.get().getHologramManager();
    }

    @Override
    public boolean isAvailable() {
        return FancyHologramsPlugin.isEnabled() && manager != null;
    }

    @Override
    public void create(HologramSpec spec) {
        if (!isAvailable()) {
            throw new IllegalStateException("FancyHolograms API недоступен");
        }

        var existing = manager.getHologram(spec.name());
        if (existing.isPresent()) {
            Hologram hologram = existing.get();
            if (hologram.getData() instanceof TextHologramData data) {
                data.setLocation(spec.location());
                data.setText(spec.lines());
                data.setPersistent(false);
                data.setTextUpdateInterval(20);
                hologram.forceUpdate();
                return;
            }
        }

        TextHologramData data = new TextHologramData(spec.name(), spec.location());
        data.setText(spec.lines());
        data.setPersistent(false);
        data.setTextUpdateInterval(20); // Update every second for timer

        Hologram hologram = manager.create(data);
        manager.addHologram(hologram);
    }

    @Override
    public void remove(String name) {
        if (manager == null || name == null || name.isBlank()) {
            return;
        }

        manager.getHologram(name).ifPresent(manager::removeHologram);
    }
}
