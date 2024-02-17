package jtetris.swing;

import java.awt.*;
import java.util.EnumMap;
import jtetris.common.BlockType;

/**
 * Associates blocks with colors.
 */
class ColorMapper {
    private final EnumMap<BlockType, Color> colors = new EnumMap<>(BlockType.class);

    /**
     * Creates a new instance of this class.
     */
    ColorMapper() {
        colors.put(BlockType.Empty, Color.WHITE);
        colors.put(BlockType.I, Color.RED.darker());
        colors.put(BlockType.J, Color.MAGENTA.darker());
        colors.put(BlockType.L, Color.YELLOW.darker());
        colors.put(BlockType.O, Color.GREEN.darker());
        colors.put(BlockType.S, Color.BLUE.darker());
        colors.put(BlockType.T, Color.ORANGE.darker());
        colors.put(BlockType.Z, Color.PINK.darker());
    }

    Color getColor(BlockType blockType) {
        return colors.get(blockType);
    }
}
