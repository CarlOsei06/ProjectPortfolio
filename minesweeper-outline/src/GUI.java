import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;

import static java.awt.Image.SCALE_SMOOTH;

/**
 * GUI for a Minesweeper game.
 * Images sourced from Wikimedia Commons:
 * <a href="https://commons.wikimedia.org/wiki/Category:Minesweeper">...</a>
 */
public class GUI extends JFrame
{
    private final int numRows, numColumns;
    private Model theModel;
    private final String imagesFolder = "images";
    private final String unrevealed = imagesFolder + "/Minesweeper_unopened_square.gif";
    private final String mine = imagesFolder + "/mine.png";
    private final JPanel buttonPanel;

    /**
     * Create a GUI with the given number of rows and columns.
     * @param numRows The number of rows.
     * @param numColumns The number of columns.
     */
    public GUI(int numRows, int numColumns)
    {
        setTitle("Minesweeper");
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.theModel = new Model(numRows, numColumns);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupMenu();

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(8, 8));
        GridLayout layout = new GridLayout(numRows, numColumns);
        layout.setHgap(2);
        layout.setVgap(2);
        buttonPanel = new JPanel(layout);

        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numColumns; col++) {
                JButton button = new MinesweeperButton(row, col);
                buttonPanel.add(button);
            }
        }
        contentPane.add(buttonPanel, BorderLayout.CENTER);
        pack();
    }

    /**
     * Start a new game.
     */
    private void restart()
    {
        theModel = new Model(numRows, numColumns);
        Component[] buttons = buttonPanel.getComponents();
        for(Component button : buttons) {
            ((MinesweeperButton) button).reset();
        }
    }

    /**
     * Set up a simple menu for starting a new game and quitting.
     */
    private void setupMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Game");
        menuBar.add(fileMenu);
        JMenuItem start = new JMenuItem("New Game");
        JMenuItem quit = new JMenuItem("Quit");
        fileMenu.add(start);
        fileMenu.add(quit);
        start.addActionListener(ev -> {
            restart();
            SwingUtilities.invokeLater(() -> getContentPane().repaint());
        });
        quit.addActionListener(ev -> System.exit(0));
    }

    /**
     * The functionality of a button.
     */
    private class MinesweeperButton extends JButton
    {
        private static final int BUTTON_SIZE = 50;
        // The icon currently displayed. This will change when
        // the location is revealed.
        private String iconName;

        public MinesweeperButton(int row, int column)
        {
            setSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setIcon(unrevealed);
            addComponentListener(new ComponentAdapter()
            {
                @Override
                public void componentResized(ComponentEvent e)
                {
                    setIcon(iconName);
                }
            });
            addActionListener(ev -> {
                if(! theModel.isRevealed(row, column)) {
                    if((ev.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                        iconName = imagesFolder + "/Minesweeper_flag.png";
                        setIcon(iconName);
                    }
                    else {
                        theModel.reveal(row, column);
                        if(theModel.hasAMine(row, column)) {
                            setIcon(mine);
                            JOptionPane.showMessageDialog(GUI.this, "Game over!");
                        }
                        else {
                            int count = theModel.countAdjacentMines(row, column);
                            String countImage = imagesFolder +
                                    "/Minesweeper_" + count + ".png";
                            setIcon(countImage);
                            if(theModel.userHasWon()) {
                                JOptionPane.showMessageDialog(GUI.this, "You win!");
                            }
                        }
                    }
                }
            });
        }

        /**
         * Reset the state of the button to be unrevealed.
         */
        public void reset()
        {
            setIcon(unrevealed);
        }

        /**
         * Set the button to the given icon and scale it
         * to the button's size.
         * @param iconName The icon filename.
         */
        private void setIcon(String iconName)
        {
            this.iconName = iconName;
            ImageIcon theIcon = new ImageIcon(iconName);
            Image img = theIcon.getImage();
            Image scaledImage = img.getScaledInstance(getWidth(), getHeight(), SCALE_SMOOTH);
            theIcon = new ImageIcon(scaledImage);
            setIcon(theIcon);
        }

    }
}
