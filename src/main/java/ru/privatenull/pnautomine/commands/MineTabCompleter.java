package ru.privatenull.pnautomine.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.privatenull.pnautomine.PnAutoMinePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tab completion for /pnautomine commands.
 */
public final class MineTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of(
            "help", "create", "delete", "reset", "resetall",
            "list", "info", "setspawn", "sethologram", "reload", "types"
    );

    private final PnAutoMinePlugin plugin;

    public MineTabCompleter(PnAutoMinePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(input)) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            String input = args[1].toLowerCase(Locale.ROOT);

            switch (sub) {
                case "delete", "remove", "reset", "info", "setspawn", "sethologram", "setholo", "settp" -> {
                    for (String id : plugin.getMineManager().getMineIds()) {
                        if (id.startsWith(input)) {
                            completions.add(id);
                        }
                    }
                }
                case "create" -> {
                    // Second arg for create is the mine ID — no completions
                }
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            String input = args[2].toLowerCase(Locale.ROOT);

            if (sub.equals("create")) {
                for (String typeId : plugin.getMineTypes().getTypeIds()) {
                    if (typeId.startsWith(input)) {
                        completions.add(typeId);
                    }
                }
            }
        }

        return completions;
    }
}
