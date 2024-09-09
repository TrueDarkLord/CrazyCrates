package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.ryderbelserion.vital.paper.api.files.CustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LegacyColorMigrator extends ICrateMigrator {

    public LegacyColorMigrator(final CommandSender sender, final MigrationType type) {
        super(sender, type);
    }

    @Override
    public void run() {
        final Collection<CustomFile> customFiles = this.plugin.getFileManager().getCustomFiles().values();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        try {
            this.config.setProperty(ConfigKeys.command_prefix, MiscUtils.convert(this.config.getProperty(ConfigKeys.command_prefix), true));
            this.config.setProperty(ConfigKeys.inventory_name, MiscUtils.convert(this.config.getProperty(ConfigKeys.inventory_name), true));

            this.config.setProperty(ConfigKeys.menu_button_name, MiscUtils.convert(this.config.getProperty(ConfigKeys.menu_button_name), true));
            this.config.setProperty(ConfigKeys.menu_button_lore, MiscUtils.convert(this.config.getProperty(ConfigKeys.menu_button_lore), true));

            this.config.setProperty(ConfigKeys.next_button_name, MiscUtils.convert(this.config.getProperty(ConfigKeys.next_button_name), true));
            this.config.setProperty(ConfigKeys.next_button_lore, MiscUtils.convert(this.config.getProperty(ConfigKeys.next_button_lore), true));

            this.config.setProperty(ConfigKeys.back_button_name, MiscUtils.convert(this.config.getProperty(ConfigKeys.back_button_name), true));
            this.config.setProperty(ConfigKeys.back_button_lore, MiscUtils.convert(this.config.getProperty(ConfigKeys.back_button_lore), true));

            this.config.setProperty(ConfigKeys.filler_name, MiscUtils.convert(this.config.getProperty(ConfigKeys.filler_name), true));
            this.config.setProperty(ConfigKeys.filler_lore, MiscUtils.convert(this.config.getProperty(ConfigKeys.filler_lore), true));

            success.add("<green>⤷ config.yml");

            this.config.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ config.yml");
        }

        try {
            for (Messages message : Messages.values()) {
                message.migrate();
            }

            success.add("<green>⤷ messages.yml");

            this.messages.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ messages.yml");
        }

        customFiles.forEach(customFile -> {
            try {
                final YamlConfiguration configuration = customFile.getConfiguration();

                if (configuration == null) return;

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) return;

                boolean isSave = false;

                if (section.contains("CrateName")) {
                    set(section, "Name", MiscUtils.convert(section.getString("CrateName", " "), true));
                    set(section, "CrateName", null);

                    isSave = true;
                } else if (section.contains("Name")) {
                    set(section, "Name", MiscUtils.convert(section.getString("Name", " "), true));

                    isSave = true;
                }

                if (section.contains("Preview-Name")) {
                    set(section, "Preview.Name", MiscUtils.convert(section.getString("Preview.Name", " "), true));
                    set(section, "Preview-Name", null);

                    isSave = true;
                } else if (section.contains("Preview.Name")) {
                    set(section, "Preview.Name", MiscUtils.convert(section.getString("Preview.Name", " "), true));

                    isSave = true;
                }

                if (section.contains("Preview.Glass.Name")) {
                    set(section, "Preview.Glass.Name", MiscUtils.convert(section.getString("Preview.Glass.Name", " "), true));

                    isSave = true;
                }

                if (section.contains("tier-preview.glass.name")) {
                    set(section, "tier-preview.glass.name", MiscUtils.convert(section.getString("tier-preview.glass.name", " "), true));

                    isSave = true;
                }

                if (section.contains("BroadCast")) {
                    set(section, "BroadCast", MiscUtils.convert(section.getString("BroadCast", " "), true));

                    isSave = true;
                }

                if (section.contains("Lore")) {
                    set(section, "Lore", MiscUtils.convert(section.getStringList("Lore"), true));

                    isSave = true;
                }

                if (section.contains("PhysicalKey")) {
                    set(section, "PhysicalKey.Name", MiscUtils.convert(section.getString("PhysicalKey.Name", " "), true));

                    set(section, "PhysicalKey.Lore", MiscUtils.convert(section.getStringList("PhysicalKey.Lore"), true));

                    isSave = true;
                }

                final ConfigurationSection tiers = section.getConfigurationSection("Tiers");

                if (tiers != null) {
                    for (String value : tiers.getKeys(false)) {
                        final ConfigurationSection tierSection = tiers.getConfigurationSection(value);

                        if (tierSection == null) continue;

                        if (tierSection.contains("Name")) {
                            set(tierSection, "Name", MiscUtils.convert(tierSection.getString("Name", " "), true));

                            isSave = true;
                        }

                        if (tierSection.contains("Lore")) {
                            set(tierSection, "Lore", MiscUtils.convert(tierSection.getStringList("Name"), true));

                            isSave = true;
                        }
                    }
                }

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (String value : prizes.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                        if (prizeSection == null) continue;

                        if (prizeSection.contains("Lore")) {
                            set(prizeSection, "DisplayLore", MiscUtils.convert(prizeSection.getStringList("Lore"), true));
                            set(prizeSection, "Lore", null);

                            isSave = true;
                        } else if (prizeSection.contains("DisplayLore")) {
                            set(prizeSection, "DisplayLore", MiscUtils.convert(prizeSection.getStringList("DisplayLore"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Messages")) {
                            set(prizeSection, "Messages", MiscUtils.convert(prizeSection.getStringList("Messages"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Items")) {
                            set(prizeSection, "Items", MiscUtils.convert(prizeSection.getStringList("Items"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Alternative-Prize.Messages")) {
                            set(prizeSection, "Alternative-Prize.Messages", MiscUtils.convert(prizeSection.getStringList("Alternative-Prize.Messages"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Alternative-Prize.Items")) {
                            set(prizeSection, "Alternative-Prize.Items", MiscUtils.convert(prizeSection.getStringList("Alternative-Prize.Items"), true));

                            isSave = true;
                        }
                    }
                }

                if (isSave) {
                    customFile.save();
                }

                success.add("<green>⤷ " + customFile.getCleanName());
            } catch (Exception exception) {
                failed.add("<red>⤷ " + customFile.getCleanName());
            }
        });

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, convertedCrates, failedCrates);

        this.fileManager.init();

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }
}