package me.post.privatemine.mine.pattern;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.RequiredArgsConstructor;
import me.post.privatemine.mine.BlockPattern;

@RequiredArgsConstructor
public class FixedBlockPattern implements BlockPattern {
    private final WrappedBlockData blockData;

    @Override
    public Generator get() {
        return () -> blockData;
    }
}
