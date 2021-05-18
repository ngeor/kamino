package net.ngeor.t3.preferences;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for PlayerPreferenceKeys.
 *
 * @author ngeor on 11/2/2018.
 */
class PlayerPreferenceKeysTest {
    @Test
    void type() {
        PlayerPreferenceKeys playerPreferenceKeys = new PlayerPreferenceKeys();
        assertEquals("pref_key_first_player_type", playerPreferenceKeys.type(PlayerName.first));
    }

    @Test
    void aILevel() {
        PlayerPreferenceKeys playerPreferenceKeys = new PlayerPreferenceKeys();
        assertEquals("pref_key_second_player_ai_level", playerPreferenceKeys.aILevel(PlayerName.second));
    }

    @Test
    void symbol() {
        PlayerPreferenceKeys playerPreferenceKeys = new PlayerPreferenceKeys();
        assertEquals("pref_key_first_player_symbol", playerPreferenceKeys.symbol(PlayerName.first));
    }
}