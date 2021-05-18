package net.ngeor.t3;

import android.view.MotionEvent;
import android.view.View;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ngeor on 3/5/2017.
 */
class CompositeTouchListenerTest {
    private View view;
    private MotionEvent motionEvent;
    private CompositeTouchListener compositeTouchListener;

    /**
     * A touch listener that handles the event.
     */
    private View.OnTouchListener handlingTouchListener;

    /**
     * A touch listener that ignores the event.
     */
    private View.OnTouchListener nonHandlingTouchListener;

    @BeforeEach
    void setUp() {
        view = mock(View.class);
        motionEvent = mock(MotionEvent.class);
        compositeTouchListener = new CompositeTouchListener();
        handlingTouchListener = mock(View.OnTouchListener.class);
        when(handlingTouchListener.onTouch(view, motionEvent)).thenReturn(true);
        nonHandlingTouchListener = mock(View.OnTouchListener.class);
        when(nonHandlingTouchListener.onTouch(view, motionEvent)).thenReturn(false);
    }

    @Test
    void onTouchWithoutListeners() {
        boolean handled = compositeTouchListener.onTouch(view, motionEvent);
        assertFalse(handled);
    }

    @Test
    void addListener() {
        // arrange
        compositeTouchListener.addListener(handlingTouchListener);

        // act
        boolean handled = compositeTouchListener.onTouch(view, motionEvent);

        // assert
        assertTrue(handled);
        verify(handlingTouchListener).onTouch(view, motionEvent);
    }

    @Test
    void removeListener() {
        // arrange
        compositeTouchListener.addListener(handlingTouchListener);

        // act
        compositeTouchListener.removeListener(handlingTouchListener);

        // assert
        boolean handled = compositeTouchListener.onTouch(view, motionEvent);
        assertFalse(handled);
    }

    @Test
    void handlingListenerSkipsNonHandlingListener() {
        // arrange
        compositeTouchListener.addListener(handlingTouchListener);
        compositeTouchListener.addListener(nonHandlingTouchListener);

        // act
        boolean handled = compositeTouchListener.onTouch(view, motionEvent);

        // assert
        assertTrue(handled);
        verify(handlingTouchListener).onTouch(view, motionEvent);
        verify(nonHandlingTouchListener, never()).onTouch(view, motionEvent);
    }

    @Test
    void nonHandlingListenerDoesNotSkipHandlingListener() {
        // arrange
        compositeTouchListener.addListener(nonHandlingTouchListener);
        compositeTouchListener.addListener(handlingTouchListener);

        // act
        boolean handled = compositeTouchListener.onTouch(view, motionEvent);

        // assert
        assertTrue(handled);
        verify(handlingTouchListener).onTouch(view, motionEvent);
        verify(nonHandlingTouchListener).onTouch(view, motionEvent);
    }
}