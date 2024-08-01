package me.post.privatemine.mine.pattern;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.RequiredArgsConstructor;
import me.post.privatemine.mine.BlockPattern;
import me.post.privatemine.mine.Cuboid;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomBlockPattern implements BlockPattern {
    @RequiredArgsConstructor
    public static class Block {
        private final float percentage;
        private final WrappedBlockData blockData;
    }

    private final List<WrappedBlockData> blocks;

    /**
     * The list of block data is filled based on the percentages of the {@link Block} list relative
     * to the {@link Cuboid} size, i.e. the amount of blocks to be generated.
     * Since the percentage relative to the size may not be exact, some blocks at the end
     * will be not covered, therefore a solution is to fill the very last using the block with the
     * highest percentage.
     * */
    public RandomBlockPattern(List<Block> blocks, Cuboid cuboid) {
        final int blocksCount = cuboid.size();
        this.blocks = new ArrayList<>(blocksCount);
        final AtomicInteger filled = new AtomicInteger(0);

        blocks.forEach(block -> {
            final int count = (int) block.percentage * blocksCount / 100;
            filled.set(filled.get() + count);

            for (int i = 0; i < count; i++) {
                this.blocks.add(block.blockData);
            }
        });

        if (filled.get() < blocksCount) {
            final Block highestChanceBlock = blocks.stream()
                .max(Comparator.comparingDouble(block -> block.percentage))
                .orElse(null);

            if (highestChanceBlock == null) {
                return;
            }

            for (int i = 0; i < blocksCount; i++) {
                this.blocks.add(highestChanceBlock.blockData);
            }
        }
    }

    @Override
    public Generator get() {
        Collections.shuffle(blocks);
        final Iterator<WrappedBlockData> blockIterator = blocks.iterator();
        return blockIterator::next;
    }
}
