package jtetris.common;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import jtetris.common.shapes.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link BlockBuilder}.
 * Created by ngeor on 16/6/2017.
 */
public class BlockBuilderTest {
    private BlockBuilder blockBuilder;

    @Before
    public void before() {
        blockBuilder = new BlockBuilder();
    }

    @Test
    public void createEmpty() {
        Shape shape = blockBuilder.create(BlockType.Empty);
        assertThat(shape, is(instanceOf(EmptyShape.class)));
    }

    @Test
    public void createI() {
        Shape shape = blockBuilder.create(BlockType.I);
        assertThat(shape, is(instanceOf(IShape.class)));
    }

    @Test
    public void createJ() {
        Shape shape = blockBuilder.create(BlockType.J);
        assertThat(shape, is(instanceOf(JShape.class)));
    }

    @Test
    public void createL() {
        Shape shape = blockBuilder.create(BlockType.L);
        assertThat(shape, is(instanceOf(LShape.class)));
    }

    @Test
    public void createO() {
        Shape shape = blockBuilder.create(BlockType.O);
        assertThat(shape, is(instanceOf(OShape.class)));
    }

    @Test
    public void createS() {
        Shape shape = blockBuilder.create(BlockType.S);
        assertThat(shape, is(instanceOf(SShape.class)));
    }

    @Test
    public void createT() {
        Shape shape = blockBuilder.create(BlockType.T);
        assertThat(shape, is(instanceOf(TShape.class)));
    }

    @Test
    public void createZ() {
        Shape shape = blockBuilder.create(BlockType.Z);
        assertThat(shape, is(instanceOf(ZShape.class)));
    }

    @Test
    public void createAllBlocksAreSupported() {
        for (BlockType blockType : BlockType.values()) {
            assertNotNull(blockType.toString(), blockBuilder.create(blockType));
        }
    }
}
