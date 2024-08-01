package me.post.privatemine;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.post.privatemine.listener.PlayerJoinListener;
import me.post.privatemine.listener.protocol.MineDigListener;
import me.post.privatemine.listener.protocol.MineInteractListener;
import me.post.privatemine.mine.BlockPattern;
import me.post.privatemine.mine.Cuboid;
import me.post.privatemine.mine.MineManager;
import me.post.privatemine.mine.pattern.FixedBlockPattern;
import me.post.privatemine.mine.pattern.RandomBlockPattern;
import me.post.privatemine.mine.pattern.SchematicBlockPattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Collectors;

public class PrivateMinePlugin extends JavaPlugin {
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        final MineManager mineManager = new MineManager(getConfig(), getBlockPattern());
        new MineInteractListener(this, mineManager).register();
        new MineDigListener(this, mineManager).register();
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, mineManager), this);
    }

    private BlockPattern getBlockPattern() {
        final ConfigurationSection strategySection = getConfig().getConfigurationSection("mine.fill-strategy");
        final String patternType = strategySection.getString("type");

        switch (patternType) {
            case "FIXED": {
                final String[] blockData = strategySection.getString("block").split(":");
                return new FixedBlockPattern(WrappedBlockData.createData(
                    Material.getMaterial(blockData[0]),
                    blockData.length == 1 ? 0 : Integer.parseInt(blockData[1])
                ));
            }

            case "RANDOM": {
                return new RandomBlockPattern(
                    strategySection.getStringList("blocks").stream()
                        .map(blockFormat -> {
                            final String[] blockData = blockFormat.split(":");
                            return new RandomBlockPattern.Block(
                                Float.parseFloat(blockData[0]),
                                WrappedBlockData.createData(
                                    Material.getMaterial(blockData[1]),
                                    blockData.length == 2 ? 0 : Integer.parseInt(blockData[2])
                                )
                            );
                        })
                        .collect(Collectors.toList()),
                    new Cuboid(
                        getConfig().getInt("mine.origin.x"),
                        getConfig().getInt("mine.origin.y"),
                        getConfig().getInt("mine.origin.z"),
                        getConfig().getInt("mine.origin.x") + getConfig().getInt("mine.size.x") - 1,
                        getConfig().getInt("mine.origin.y") + getConfig().getInt("mine.size.y") - 1,
                        getConfig().getInt("mine.origin.z") + getConfig().getInt("mine.size.z") - 1
                    )
                );
            }

            case "SCHEMATIC": {
                return new SchematicBlockPattern(new File(getDataFolder(), strategySection.getString("file")));
            }
        }

        final WrappedBlockData air = WrappedBlockData.createData(Material.STONE);
        Bukkit.getLogger().warning("Pattern type '" + patternType + "' unknown. Using plugin defaults instead.");
        return () -> () -> air;
    }
}
