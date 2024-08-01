package me.post.privatemine.listener.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.RequiredArgsConstructor;
import me.post.privatemine.mine.Mine;
import me.post.privatemine.mine.MineManager;
import me.post.privatemine.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class MineDigListener {
    private final Plugin plugin;
    private final MineManager mineManager;
    private static final short AIR_BLOCK = BlockUtil.unwrap(WrappedBlockData.createData(Material.AIR));

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                final BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);
                final Mine mine = mineManager.getMine(event.getPlayer());

                if (mine == null || mine.outside(position)) {
                    return;
                }

                if (event.getPacket().getEnumModifier(EnumWrappers.PlayerDigType.class, 2).read(0) != EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                    return;
                }

                mine.setBlock(position, AIR_BLOCK);
            }
        });
    }
}
