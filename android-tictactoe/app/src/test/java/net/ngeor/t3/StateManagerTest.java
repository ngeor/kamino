package net.ngeor.t3;

import android.os.Bundle;

import net.ngeor.t3.models.BoardModel;
import net.ngeor.t3.models.GameState;
import net.ngeor.t3.models.PlayerSymbol;
import net.ngeor.t3.settings.Settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for StateManager.
 * Created by ngeor on 2/7/2017.
 */
class StateManagerTest {
    private Bundle bundle;
    private StateManager stateManager;

    @BeforeEach
    void before() {
        bundle = mock(Bundle.class);
        stateManager = new StateManager(bundle);
    }

    @Test
    void getSettings() {
        Settings settings = mock(Settings.class);
        when(bundle.getSerializable("settings")).thenReturn(settings);
        Settings actualSettings = stateManager.getSettings();
        assertEquals(settings, actualSettings);
    }

    @Test
    void setSettings() {
        Settings settings = mock(Settings.class);
        stateManager.setSettings(settings);
        verify(bundle).putSerializable("settings", settings);
    }

    @Test
    void getBoardModel() {
        BoardModel boardModel = mock(BoardModel.class);
        when(bundle.getSerializable("boardModel")).thenReturn(boardModel);
        BoardModel actualBoardModel = stateManager.getBoardModel();
        assertEquals(boardModel, actualBoardModel);
    }

    @Test
    void setBoardModel() {
        BoardModel boardModel = mock(BoardModel.class);
        stateManager.setBoardModel(boardModel);
        verify(bundle).putSerializable("boardModel", boardModel);
    }

    @Test
    void getGameState() {
        when(bundle.getString("gameState")).thenReturn("Draw");
        GameState state = stateManager.getGameState();
        assertEquals(GameState.Draw, state);
    }

    @Test
    void getGameState_whenBundleHasNullValue() {
        when(bundle.getString("gameState")).thenReturn(null);
        GameState state = stateManager.getGameState();
        assertEquals(GameState.NotStarted, state);
    }

    @Test
    void getGameState_whenBundleHasUnknownValue() {
        when(bundle.getString("gameState")).thenReturn("Oops");
        GameState state = stateManager.getGameState();
        assertEquals(GameState.NotStarted, state);
    }

    @Test
    void setGameState() {
        stateManager.setGameState(GameState.Draw);
        verify(bundle).putString("gameState", "Draw");
    }

    @Test
    void getPlayerSymbol() {
        when(bundle.getString("turn")).thenReturn("O");
        PlayerSymbol playerSymbol = stateManager.getTurn();
        assertEquals(PlayerSymbol.O, playerSymbol);
    }

    @Test
    void getPlayerSymbol_whenBundleHasNullValue() {
        when(bundle.getString("turn")).thenReturn(null);
        PlayerSymbol playerSymbol = stateManager.getTurn();
        assertNull(playerSymbol);
    }

    @Test
    void getPlayerSymbol_whenBundleHasUnknownValue() {
        when(bundle.getString("turn")).thenReturn("Oops");
        PlayerSymbol playerSymbol = stateManager.getTurn();
        assertNull(playerSymbol);
    }

    @Test
    void setPlayerSymbol() {
        stateManager.setTurn(PlayerSymbol.O);
        verify(bundle).putString("turn", "O");
    }

    @Test
    void setPlayerSymbol_Null() {
        stateManager.setTurn(null);
        verify(bundle).putString("turn", "");
    }
}
