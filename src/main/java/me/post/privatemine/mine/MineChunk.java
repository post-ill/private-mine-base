package me.post.privatemine.mine;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.post.privatemine.util.BlockUtil;

@Accessors(fluent = true)
public class MineChunk {
    @Getter
    private final PacketContainer packet;
    private final Cuboid cuboid;
    private final short[] blocks;

    public MineChunk(PacketContainer packet, Cuboid cuboid) {
        this.packet = packet;
        this.cuboid = cuboid;
        blocks = new short[
            (cuboid.maxX() - cuboid.minX() + 1) *
            (cuboid.maxY() - cuboid.minY() + 1) *
            (cuboid.maxZ() - cuboid.minZ() + 1)
        ];

        for (MultiBlockChangeInfo blockChange : packet.getMultiBlockChangeInfoArrays().read(0)) {
            setBlock(blockChange.getAbsoluteX(), blockChange.getY(), blockChange.getAbsoluteZ(), BlockUtil.unwrap(blockChange.getData()));
        }
    }

    public void setBlock(BlockPosition position, short block) {
        setBlock(position.getX(), position.getY(), position.getZ(), block);
    }

    private void setBlock(int x, int y, int z, short block) {
        blocks[
            x - cuboid.minX() +
            (y - cuboid.minY()) * cuboid.sizeX() +
            (z - cuboid.minZ()) * cuboid.sizeX() * cuboid.sizeY()
        ] = block;
    }

    public short getBlock(BlockPosition position) {
        return blocks[
            position.getX() - cuboid.minX() +
            (position.getY() - cuboid.minY()) * cuboid.sizeX() +
            (position.getZ() - cuboid.minZ()) * cuboid.sizeX() * cuboid.sizeY()
        ];
    }
}
