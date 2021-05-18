package net.ngeor.t3;

import android.content.Context;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.MutableGameModel;
import net.ngeor.t3.models.PlayerSymbol;
import net.ngeor.t3.players.AIPlayer;
import net.ngeor.t3.players.HumanPlayer;
import net.ngeor.t3.settings.AIPlayerDefinition;
import net.ngeor.t3.settings.HumanPlayerDefinition;
import net.ngeor.t3.settings.PlayerDefinitions;
import net.ngeor.t3.settings.Settings;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PlayerFactory.
 *
 * @author ngeor on 10/2/2018.
 */
class PlayerFactoryTest {
    @Test
    void createPlayers() {
        Context context = mock(Context.class);
        MutableGameModel model = mock(MutableGameModel.class);
        MessageBox messageBox = mock(MessageBox.class);
        CompositeTouchListener boardTouchListener = mock(CompositeTouchListener.class);
        PlayerFactory playerFactory = new PlayerFactory(context, model, messageBox, boardTouchListener);

        Settings settings = new Settings(3, 3, false, new PlayerDefinitions(
                new HumanPlayerDefinition(PlayerSymbol.X),
                new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY)));
        when(model.getSettings()).thenReturn(settings);

        // act
        playerFactory.createPlayers();

        // assert
        verify(boardTouchListener).addListener(any(HumanPlayer.class));
        verify(model).addGameModelListener(any(HumanPlayer.class));
        verify(model).addGameModelListener(any(AIPlayer.class));
    }

    @Test
    void destroyPlayers() {
        Context context = mock(Context.class);
        MutableGameModel model = mock(MutableGameModel.class);
        MessageBox messageBox = mock(MessageBox.class);
        CompositeTouchListener boardTouchListener = mock(CompositeTouchListener.class);
        PlayerFactory playerFactory = new PlayerFactory(context, model, messageBox, boardTouchListener);

        Settings settings = new Settings(3, 3, false, new PlayerDefinitions(
                new HumanPlayerDefinition(PlayerSymbol.X),
                new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY)));
        when(model.getSettings()).thenReturn(settings);
        playerFactory.createPlayers();

        // act
        playerFactory.destroyPlayers();

        // assert
        verify(boardTouchListener).removeListener(any(HumanPlayer.class));
        verify(model).removeGameModelListener(any(HumanPlayer.class));
        verify(model).removeGameModelListener(any(AIPlayer.class));
    }
}
