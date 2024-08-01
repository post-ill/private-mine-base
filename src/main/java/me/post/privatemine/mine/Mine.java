package me.post.privatemine.mine;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

public class Mine {
    private final Player owner;
    private final Cuboid cuboid;
    private final MineChunk[][] chunks;
    private final BlockPattern blockPattern;

    public Mine(Player owner, Cuboid cuboid, BlockPattern blockPattern) {
        this.owner = owner;
        this.cuboid = cuboid;
        chunks = new MineChunk[((cuboid.maxX() - cuboid.minX() + 1) >> 4) + 1][((cuboid.maxZ() - cuboid.minZ() + 1) >> 4) + 1];
        this.blockPattern = blockPattern;
        reset();
    }

    public boolean outside(BlockPosition position) {
        return
            !(position.getX() >= cuboid.minX() && position.getX() <= cuboid.maxX() &&
            position.getY() >= cuboid.minY() && position.getY() <= cuboid.maxY() &&
            position.getZ() >= cuboid.minZ() && position.getZ() <= cuboid.maxZ());
    }

    public void setBlock(BlockPosition position, short block) {
        chunks[(position.getX() >> 4) - (cuboid.minX() >> 4)][(position.getZ() >> 4) - (cuboid.minZ() >> 4)].setBlock(position, block);
    }

    public short getBlock(BlockPosition position) {
        return chunks[(position.getX() >> 4) - (cuboid.minX() >> 4)][(position.getZ() >> 4) - (cuboid.minZ() >> 4)].getBlock(position);
    }

    public void reset() {
        if (chunks[0][0] == null) {
            buildChunks();
        }

        for (MineChunk[] pathArray : chunks) {
            for (MineChunk chunk : pathArray) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(owner, chunk.packet());
            }
        }
    }

    private void buildChunks() {
        final BlockPattern.Generator blockGenerator = blockPattern.get();
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(cuboid.iterator(), Spliterator.ORDERED), false)
            .map(position -> new MultiBlockChangeInfo(position.toLocation(null), blockGenerator.next()))
            .collect(groupingBy(
                blockChange -> ((long) blockChange.getChunk().getChunkX() << 32) | blockChange.getChunk().getChunkZ(),
                toCollection(LinkedList::new)
            ))
            .forEach((hashedChunk, changes) -> {
                final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
                final MultiBlockChangeInfo minBlock = changes.get(0);
                packet.getChunkCoordIntPairs().write(0, minBlock.getChunk());
                packet.getMultiBlockChangeInfoArrays().write(0, changes.toArray(new MultiBlockChangeInfo[0]));
                chunks
                    [minBlock.getChunk().getChunkX() - (cuboid.minX() >> 4)]
                    [minBlock.getChunk().getChunkZ() - (cuboid.minZ() >> 4)] = new MineChunk(
                        packet,
                        new Cuboid(
                            minBlock.getAbsoluteX(),
                            minBlock.getY(),
                            minBlock.getAbsoluteZ(),
                            Math.min(minBlock.getChunk().getChunkX() + 1 << 4, cuboid.maxX()),
                            cuboid.maxY(),
                            Math.min(minBlock.getChunk().getChunkZ() + 1 << 4, cuboid.maxZ())
                        )
                    );
            });
    }
}
