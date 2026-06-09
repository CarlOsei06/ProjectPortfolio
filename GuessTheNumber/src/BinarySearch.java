
public class BinarySearch {
public void guessingMethod(int valueToFind, int low, int high, int count) {
    if (low > high) {
        System.out.println("The value " + valueToFind + " was not found.");
        return;
    }

    int guess = (low + high) / 2;
    count++;
    System.out.println("The guess is " + guess);

    if (guess == valueToFind) {
        System.out.println("Correct! The number was " + valueToFind + ". It took " + count + " guesses.");
    }
    else if (guess > valueToFind) {
        Recursions.tooHigh();
        guessingMethod(valueToFind, low, guess - 1, count);
    } else {
        Recursions.tooLow();
        guessingMethod(valueToFind, guess + 1, high, count);
    }
}
}