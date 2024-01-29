package jtetris.swing;

/**
 * Listens to events from the keyboard.
 */
interface GameKeyListener {
    void onLeft();

    void onRight();

    void onUp();

    void onDown();

    void onSpace();

    void onS();
}
