package net.ngeor.t3.preferences;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for PlayerName.
 *
 * @author ngeor on 11/2/2018.
 */
class PlayerNameTest {
    @Test
    void otherOfFirstIsSecond() {
        assertEquals(PlayerName.second, PlayerName.first.other());
    }

    @Test
    void otherOfSecondIsFirst() {
        assertEquals(PlayerName.first, PlayerName.second.other());
    }
}