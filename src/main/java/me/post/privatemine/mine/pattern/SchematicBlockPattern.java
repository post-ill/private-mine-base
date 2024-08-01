package me.post.privatemine.mine.pattern;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import de.tr7zw.nbtapi.NBTFile;
import me.post.privatemine.mine.BlockPattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class SchematicBlockPattern implements BlockPattern {
    private final WrappedBlockData[] blocks;

    /**
     * Unlike the cuboid, the schematic block positions have an order of X -> Z -> Y.
     * Therefore, there is a need of remapping to the cuboid order, that is X -> Y -> Z.
     * */
    @SuppressWarnings("deprecation")
    public SchematicBlockPattern(File schematicFile) {
        final NBTFile nbtFile;

        try {
            nbtFile = new NBTFile(schematicFile);
        } catch (IOException e) {
            blocks = new WrappedBlockData[0];
            Bukkit.getLogger().warning("Could not load nbt file '" + schematicFile.getName() + "'.");
            return;
        }

        final short width = nbtFile.getShort("Width");
        final short length = nbtFile.getShort("Length");
        final short height = nbtFile.getShort("Height");
        final byte[] blocks = requireNonNull(nbtFile.getByteArray("Blocks"));
        final byte[] data = requireNonNull(nbtFile.getByteArray("Data"));
        this.blocks = new WrappedBlockData[width * height * length];

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int index = x + z * width + y * length * width;
                    final WrappedBlockData blockData = WrappedBlockData.createData(Material.getMaterial(blocks[index]), data[index]);
                    final int cuboidIndex = x + y * width + z * width * height;
                    this.blocks[cuboidIndex] = blockData;
                }
            }
        }
    }

    @Override
    public Generator get() {
        return new Generator() {
            int index;

            @Override
            public WrappedBlockData next() {
                return blocks[index++];
            }
        };
    }
}
