package me.post.privatemine.listener;

import lombok.RequiredArgsConstructor;
import me.post.privatemine.mine.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private final Plugin plugin;
    private final MineManager mineManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.hidePlayer(event.getPlayer());
                event.getPlayer().hidePlayer(player);
            });
            mineManager.add(event.getPlayer());
        }, 40L);
    }
}
