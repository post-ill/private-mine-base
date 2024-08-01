package me.post.privatemine.mine;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MineManager {
    private final FileConfiguration config;
    private final BlockPattern blockPattern;
    private final Map<String, Mine> mines = new HashMap<>();

    public void add(Player player) {
        final Cuboid cuboid = new Cuboid(
            config.getInt("mine.origin.x"),
            config.getInt("mine.origin.y"),
            config.getInt("mine.origin.z"),
            config.getInt("mine.origin.x") + config.getInt("mine.size.x") - 1,
            config.getInt("mine.origin.y") + config.getInt("mine.size.y") - 1,
            config.getInt("mine.origin.z") + config.getInt("mine.size.z") - 1
        );
        mines.put(player.getName(), new Mine(player, cuboid, blockPattern));
    }

    public Mine getMine(Player player) {
        return mines.get(player.getName());
    }
}
