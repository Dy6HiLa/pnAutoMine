package ru.privatenull.pnautomine.hologram;

import org.bukkit.Location;

import java.util.List;

public record HologramSpec(
        String name,
        Location location,
        List<String> lines
) {
}
