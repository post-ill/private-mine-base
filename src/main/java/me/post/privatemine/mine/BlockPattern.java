package me.post.privatemine.mine;

import com.comphenix.protocol.wrappers.WrappedBlockData;

import java.util.function.Supplier;

/**
 * Acts as a block generator provider.
 * The block position is relative to the cuboid fill order, where the first covered dimension is X, followed
 * by Y and Z. So when the max X is reached, the next increment goes to the Y. When max Y is reached, the
 * increment goes to the Z.
 * */
@FunctionalInterface
public interface BlockPattern extends Supplier<BlockPattern.Generator> {
    @FunctionalInterface
    interface Generator {
        WrappedBlockData next();
    }
}
