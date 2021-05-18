package net.ngeor.t3.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for WinningSequencesProvider.
 *
 * @author ngeor on 10/2/2018.
 */
class WinningSequencesProviderTest {
    private List<List<Location>> sequences;

    @BeforeEach
    void before() {
        // arrange
        BoardModel boardModel = new BoardModel(3, 3);
        WinningSequencesProvider winningSequencesProvider = new WinningSequencesProvider();

        // act
        sequences = winningSequencesProvider.calculate(boardModel);
    }

    @Test
    void shouldNotBeNull() {
        assertNotNull(sequences);
    }

    @Test
    void shouldBeEight() {
        assertEquals(8, sequences.size());
    }

    @Test
    void shouldIncludeHorizontal1() {
        assertTrue(sequences.stream().anyMatch(l -> isHorizontalSequence(l, 0)));
    }

    @Test
    void shouldIncludeHorizontal2() {
        assertTrue(sequences.stream().anyMatch(l -> isHorizontalSequence(l, 1)));
    }

    @Test
    void shouldIncludeHorizontal3() {
        assertTrue(sequences.stream().anyMatch(l -> isHorizontalSequence(l, 2)));
    }

    @Test
    void shouldIncludeVertical1() {
        assertTrue(sequences.stream().anyMatch(l -> isVerticalSequence(l, 0)));
    }

    @Test
    void shouldIncludeVertical2() {
        assertTrue(sequences.stream().anyMatch(l -> isVerticalSequence(l, 1)));
    }

    @Test
    void shouldIncludeVertical3() {
        assertTrue(sequences.stream().anyMatch(l -> isVerticalSequence(l, 2)));
    }

    @Test
    void shouldIncludeBackslash() {
        assertTrue(sequences.stream().anyMatch(this::isBackslash));
    }

    @Test
    void shouldIncludeSlash() {
        assertTrue(sequences.stream().anyMatch(this::isSlash));
    }

    private boolean isHorizontalSequence(List<Location> list, int row) {
        for (int col = 0; col < 3; col++) {
            if (!list.get(col).equals(new Location(row, col))) {
                return false;
            }
        }

        return true;
    }

    private boolean isVerticalSequence(List<Location> list, int col) {
        for (int row = 0; row < 3; row++) {
            if (!list.get(row).equals(new Location(row, col))) {
                return false;
            }
        }

        return true;
    }

    private boolean isBackslash(List<Location> list) {
        for (int i = 0; i < 3; i++) {
            if (!list.get(i).equals(new Location(i, i))) {
                return false;
            }
        }

        return true;
    }

    private boolean isSlash(List<Location> list) {
        for (int i = 0; i < 3; i++) {
            if (!list.get(i).equals(new Location(2 - i, i))) {
                return false;
            }
        }

        return true;
    }
}
