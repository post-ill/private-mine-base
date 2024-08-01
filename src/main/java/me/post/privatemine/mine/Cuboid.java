package me.post.privatemine.mine;

import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Iterator;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class Cuboid implements Iterable<BlockPosition> {
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public int sizeX() {
        return maxX - minX + 1;
    }

    public int sizeY() {
        return maxY - minY + 1;
    }

    public int sizeZ() {
        return maxZ - minZ + 1;
    }

    public int size() {
        return sizeX() * sizeY() * sizeZ();
    }

    @Override
    public Iterator<BlockPosition> iterator() {
        return new Iterator<BlockPosition>() {
            private int x = minX - 1;
            private int y = minY;
            private int z = minZ;

            @Override
            public boolean hasNext() {
                if (x < maxX) {
                    x++;
                    return true;
                }

                if (y < maxY) {
                    x = minX;
                    y++;
                    return true;
                }

                if (z < maxZ) {
                    x = minX;
                    y = minY;
                    z++;
                    return true;
                }

                return false;
            }

            @Override
            public BlockPosition next() {
                return new BlockPosition(x, y, z);
            }
        };
    }
}
