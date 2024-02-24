package rules;

/**
 * Demonstrates a long line.
 */
class LineLengthTooLong {
    public void example() {
        // the following should give an error
        System.out.println("This is a very long string that should be reduced a bit because it is over 120 characters.");
    }
}
