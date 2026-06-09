import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public static void main(String[] args) {
Random rand = new Random();
Scanner scan = new Scanner(System.in);
int range = 0;
boolean validInput = false;

while (!validInput) {
    System.out.println("Enter the end range 10, 100 or 1000: ");
    try {
        range = scan.nextInt();
        if (range == 10 || range == 100 || range == 1000) {
            validInput = true;
        } else {
            System.out.println("Invalid choice. Please enter 10, 100, or 1000.");
        }
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a number.");
        scan.next(); // Clear the invalid input
    }
}

int valueToFind = rand.nextInt(range) + 1; // Generates a random number between 1 and the end range
System.out.println("The secret number is: " + valueToFind);

guessNumber(valueToFind, range);
}

private static void guessNumber(int valueToFind, int range) {
BinarySearch binarySearch = new BinarySearch();
binarySearch.guessingMethod(valueToFind, 1, range, 0);
}