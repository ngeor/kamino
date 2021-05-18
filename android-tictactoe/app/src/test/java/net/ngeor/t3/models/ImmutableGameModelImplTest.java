package net.ngeor.t3.models;

import net.ngeor.t3.settings.HumanPlayerDefinition;
import net.ngeor.t3.settings.PlayerDefinitions;
import net.ngeor.t3.settings.Settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for ImmutableGameModelImpl.
 *
 * @author ngeor on 10/2/2018.
 */

class ImmutableGameModelImplTest {

    @Nested
    class Draw {
        private Settings settings;

        @BeforeEach
        void before() {
            settings = new Settings(3, 3, false, new PlayerDefinitions(
                    new HumanPlayerDefinition(PlayerSymbol.X),
                    new HumanPlayerDefinition(PlayerSymbol.O)));
        }

        @Test
        void draw() {
            ImmutableGameModel model = new ImmutableGameModelImpl(settings);
            model = model.immutableStart()
                    .immutablePlay(0, 0)
                    .immutablePlay(1, 0)
                    .immutablePlay(2, 0)
                    .immutablePlay(1, 1)
                    .immutablePlay(0, 1)
                    .immutablePlay(2, 1)
                    .immutablePlay(1, 2)
                    .immutablePlay(0, 2)
                    .immutablePlay(2, 2);
            assertEquals(GameState.Draw, model.getState());
        }
    }

    @Nested
    class NewModel {
        private Settings settings;
        private ImmutableGameModel model;

        @BeforeEach
        void before() {
            settings = new Settings(3, 3, false, new PlayerDefinitions(
                    new HumanPlayerDefinition(PlayerSymbol.X),
                    new HumanPlayerDefinition(PlayerSymbol.O)));
            model = new ImmutableGameModelImpl(settings);
        }

        @Test
        void settingsShouldBeSame() {
            assertSame(settings, model.getSettings());
        }

        @Test
        void boardModelShouldBeEmpty() {
            BoardModel boardModel = model.getBoardModel();
            assertEquals(3, boardModel.getCols());
            assertEquals(3, boardModel.getRows());
            assertEquals(boardModel.allLocations(), boardModel.emptyLocations());
        }

        @Test
        void stateShouldBeNotStarted() {
            assertEquals(GameState.NotStarted, model.getState());
        }

        @Test
        void cannotPlayOnNonStartedGame() {
            assertThrows(IllegalStateException.class, () ->             model.immutablePlay(0, 0));
        }
    }

    @Nested
    class StartedModel {
        private ImmutableGameModel model;

        @BeforeEach
        void before() {
            Settings settings = new Settings(3, 3, false, new PlayerDefinitions(
                    new HumanPlayerDefinition(PlayerSymbol.X),
                    new HumanPlayerDefinition(PlayerSymbol.O)));
            model = new ImmutableGameModelImpl(settings);
            model = model.immutableStart();
        }

        @Test
        void stateShouldBeWaitingForPlayer() {
            assertEquals(GameState.WaitingPlayer, model.getState());
        }

        @Test
        void turnShouldBePlayerX() {
            assertEquals(PlayerSymbol.X, model.getTurn());
        }

        @Test
        void canPlay() {
            ImmutableGameModel nextModel = model.immutablePlay(0, 0);
            assertNotNull(nextModel);
            assertEquals(PlayerSymbol.X, model.getTurn());
            assertEquals(PlayerSymbol.O, nextModel.getTurn());
        }
    }
}
