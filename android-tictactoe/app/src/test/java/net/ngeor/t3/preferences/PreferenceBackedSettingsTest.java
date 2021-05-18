package net.ngeor.t3.preferences;

import android.content.SharedPreferences;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.PlayerSymbol;
import net.ngeor.t3.settings.AIPlayerDefinition;
import net.ngeor.t3.settings.HumanPlayerDefinition;
import net.ngeor.t3.settings.PlayerDefinitions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PreferenceBackedSettings.
 * Created by ngeor on 2/11/2017.
 */
class PreferenceBackedSettingsTest {
    private SharedPreferences sharedPreferences;
    private PreferenceBackedSettings settings;

    @BeforeEach
    void setUp() {
        sharedPreferences = mock(SharedPreferences.class);
        settings = new PreferenceBackedSettings(sharedPreferences);

        // mock default values
        mockPreference("pref_key_first_player_symbol", "X", "X");
        mockPreference("pref_key_first_player_type", "HUMAN", "HUMAN");
        mockPreference("pref_key_first_player_ai_level", "EASY", null);

        mockPreference("pref_key_second_player_symbol", "O", "O");
        mockPreference("pref_key_second_player_type", "CPU", "CPU");
        mockPreference("pref_key_second_player_ai_level", "EASY", "EASY");
    }

    @Test
    void getRows() {
        assertEquals(3, settings.createSettings().getRows());
    }

    @Test
    void getCols() {
        assertEquals(3, settings.createSettings().getCols());
    }

    @Test
    void getPlayerDefinitions_withDefaultSettings() {
        // arrange

        // act
        PlayerDefinitions playerDefinitions = settings.createSettings().getPlayerDefinitions();

        // assert
        assertEquals(
                new PlayerDefinitions(
                        new HumanPlayerDefinition(PlayerSymbol.X),
                        new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY)),
                playerDefinitions);
    }

    @Test
    void getPlayerDefinitions_withCustomSettings() {
        // arrange
        mockPreference("pref_key_first_player_symbol", "X", "O");
        mockPreference("pref_key_first_player_type", "HUMAN", "CPU");
        mockPreference("pref_key_first_player_ai_level", "EASY", "MEDIUM");

        mockPreference("pref_key_second_player_symbol", "O", "X");
        mockPreference("pref_key_second_player_type", "CPU", "HUMAN");
        mockPreference("pref_key_second_player_ai_level", "EASY", "EASY");

        // act
        PlayerDefinitions playerDefinitions = settings.createSettings().getPlayerDefinitions();

        // assert
        assertEquals(
                new PlayerDefinitions(
                        new AIPlayerDefinition(PlayerSymbol.O, AILevel.MEDIUM),
                        new HumanPlayerDefinition(PlayerSymbol.X)),
                playerDefinitions);
    }

    private void mockPreference(String key, String defaultValue, String value) {
        when(sharedPreferences.getString(key, defaultValue)).thenReturn(value);
    }
}