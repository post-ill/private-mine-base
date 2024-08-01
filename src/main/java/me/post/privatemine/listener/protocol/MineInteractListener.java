package me.post.privatemine.listener.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.RequiredArgsConstructor;
import me.post.privatemine.mine.Mine;
import me.post.privatemine.mine.MineManager;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class MineInteractListener {
    private final Plugin plugin;
    private final MineManager mineManager;

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                final Mine mine = mineManager.getMine(event.getPlayer());

                if (mine == null || mine.outside(event.getPacket().getBlockPositionModifier().read(0))) {
                    return;
                }

                event.setCancelled(true);
            }
        });
    }
}
