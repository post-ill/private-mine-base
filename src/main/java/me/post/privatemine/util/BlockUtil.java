package me.post.privatemine.util;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;

public class BlockUtil {
    @SuppressWarnings("deprecation")
    public static WrappedBlockData wrap(short blockData) {
        return WrappedBlockData.createData(Material.getMaterial(blockData >> 4), blockData & 15);
    }

    @SuppressWarnings("deprecation")
    public static short unwrap(WrappedBlockData wrapped) {
        return (short) (wrapped.getType().getId() << 4 | (wrapped.getData() & 15));
    }
}
