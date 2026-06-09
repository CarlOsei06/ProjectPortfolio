import java.util.Random;

/**
 * The game model.
 * A 2D grid containing some mines.
 */
public class Model {
    // The number of rows and columns in the game.
    private final int numRows, numColumns;
    // The number of mines.
    private final int numMines;
    // Support random placement of the mines.
    private final Random rand = new Random();
    // TODO: Define a 2D boolean array to record where the mines are.

    private boolean[][] grid;
    private boolean[][] revealed;

    /**
     * Set up the playing space with a random number of mines.
     *
     * @param numRows    The number of rows.
     * @param numColumns The number of columns.
     */
    public Model(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        numMines = numColumns * numRows / 10;
        // TODO: Create the 2D boolean array that records where the mines are.
        grid = new boolean[numRows][numColumns];
        revealed = new boolean[numRows][numColumns];
        placeTheMines();
    }

    /**
     * TODO: Complete this method.
     * Place the required number of mines in the grid.
     * Do this randomly, if possible.
     *
     * @return
     */
    public int placeTheMines() {
        // TODO: Either place 'numMines' in fixed locations, or
        // randomly generate the locations.
        int count = 0;
        while (count < numMines) {
            int row = rand.nextInt(numRows);
            int column = rand.nextInt(numColumns);
            if (!grid[row][column]) {
                grid[row][column] = true;
                count++;
            }

        }
        return count;
    }

    /**
     * Whether this square has a mine or not.
     * TODO: Complete this method.
     *
     * @param row    The row.
     * @param column The column.
     * @return true if there is a mine there.
     */
    public boolean hasAMine(int row, int column) {
        // TODO: Replace this random value with whether there is
        // a mine in the location.
        return grid[row][column];

    }


    /**
     * TODO: Complete this method.
     * Return how many mines are in the adjacent squares.
     *
     * @param theRow    The row.
     * @param theColumn The column.
     * @return the count.
     */
    public int countAdjacentMines(int theRow, int theColumn) {
        int count = 0;
        for (int row = theRow - 1; row <= theRow + 1; row++) {
            for (int column = theColumn - 1; column <= theColumn + 1; column++) {
                // TODO: Make sure that [row][column] is valid and, if it is,
                // check whether there is a mine in that location.
                // Update count if there is.
                if (row >= 0 &&
                        row < numRows &&
                        column >= 0
                        && column < numColumns) {
                    if(grid[row][column]) {
                        count++;
                    }

                }
            }
        }
        // TODO: Replace the random value with a proper count of the mines.
        return count;
    }

    /**
     * Note that this square has been revealed.
     * TODO: Complete this method.
     *
     * @param row    The row.
     * @param column The column.
     */
    public void reveal(int row, int column) {
        // TODO: Record that this location is now revealed.
        revealed[row][column] = true;
    }

    /**
     * Whether this square has been revealed or not.
     * TODO: Complete this method.
     *
     * @param row    The row.
     * @param column The column.
     * @return true if the square has been revealed
     */
    public boolean isRevealed(int row, int column) {
        // TODO: Replace the false value with whether this location
        // has already been revealed.
        return revealed[row][column];
    }

    /**
     * TODO: Complete this method.
     * Whether the use has won.
     * If all the squares have been revealed except those with
     * mines then the user has won and the game is over.
     *
     * @return true if they have won.
     */
    public boolean userHasWon() {
        // TODO: Replace the false value with whether the number of unrevealed
        // locations is the same as the number of mines ('numMines').
        int count = 0;
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                if (revealed[row][column]) {
                    count++;
                }
            }
        }
        return numRows * numColumns - count == numMines;

        }
    }

