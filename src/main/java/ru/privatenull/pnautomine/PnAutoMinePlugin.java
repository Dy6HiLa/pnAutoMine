package ru.privatenull.pnautomine;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import ru.privatenull.pnautomine.commands.MineCommand;
import ru.privatenull.pnautomine.commands.MineTabCompleter;
import ru.privatenull.pnautomine.config.MessagesConfig;
import ru.privatenull.pnautomine.config.MineTypesConfig;
import ru.privatenull.pnautomine.hologram.HologramService;
import ru.privatenull.pnautomine.listeners.BlockBreakListener;
import ru.privatenull.pnautomine.mine.MineManager;
import ru.privatenull.pnautomine.placeholder.MinePlaceholderExpansion;
import ru.privatenull.pnlibrary.lifecycle.PluginBanner;
import ru.privatenull.pnlibrary.update.UpdateChecker;
import ru.privatenull.pnlibrary.update.UpdateSettings;

public final class PnAutoMinePlugin extends JavaPlugin {

    public static final String SUPPORT_DISCORD = "https://discord.gg/rRbzq6cnc6";
    private static final String UPDATE_REPOSITORY = "Dy6HiLa/pnAutoMine";
    private static final String UPDATE_PERMISSION = "pnautomine.admin";
    private static final long UPDATE_CHECK_PERIOD_HOURS = 12L;

    private MessagesConfig messages;
    private MineTypesConfig mineTypes;
    private MineManager mineManager;
    private HologramService holograms;
    private UpdateChecker updateChecker;
    private Metrics metrics;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        messages = new MessagesConfig(this);
        mineTypes = new MineTypesConfig(this);
        holograms = new HologramService(this);

        mineManager = new MineManager(this);
        mineManager.loadMines();
        metrics = new Metrics(this, 32828);
        updateChecker = createUpdateChecker();
        if (updateChecker != null) {
            updateChecker.start();
        }

        var cmd = getCommand("pnautomine");
        if (cmd == null) {
            throw new IllegalStateException("Команда pnautomine отсутствует в plugin.yml");
        }
        var executor = new MineCommand(this);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(new MineTabCompleter(this));

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MinePlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI подключён.");
        }

        PluginBanner.enabled(this, SUPPORT_DISCORD);
    }

    @Override
    public void onDisable() {
        if (mineManager != null) {
            mineManager.shutdown();
        }
        if (holograms != null) {
            holograms.shutdown();
        }
        if (updateChecker != null) {
            updateChecker.cancel();
            updateChecker = null;
        }
        metrics = null;

        PluginBanner.disabled(this, SUPPORT_DISCORD);
    }

    public void reloadPlugin() {
        reloadConfig();
        messages.load();
        mineTypes.reload();

        if (mineManager != null) {
            mineManager.shutdown();
        }
        mineManager = new MineManager(this);
        mineManager.loadMines();

        if (holograms != null) {
            holograms.shutdown();
        }
        holograms = new HologramService(this);
        mineManager.syncHolograms();

        if (updateChecker != null) {
            updateChecker.restart(createUpdateSettings());
        } else {
            updateChecker = createUpdateChecker();
            if (updateChecker != null) {
                updateChecker.start();
            }
        }
    }

    public MessagesConfig getMessages() {
        return messages;
    }

    public MineTypesConfig getMineTypes() {
        return mineTypes;
    }

    public MineManager getMineManager() {
        return mineManager;
    }

    public HologramService getHolograms() {
        return holograms;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    private UpdateChecker createUpdateChecker() {
        try {
            return new UpdateChecker(this, createUpdateSettings());
        } catch (IllegalArgumentException ex) {
            getLogger().warning("Не удалось включить автообновление: " + ex.getMessage());
            return null;
        }
    }

    private UpdateSettings createUpdateSettings() {
        return new UpdateSettings(
                true,
                UPDATE_REPOSITORY,
                UPDATE_PERMISSION,
                UPDATE_CHECK_PERIOD_HOURS,
                SUPPORT_DISCORD
        );
    }
}
