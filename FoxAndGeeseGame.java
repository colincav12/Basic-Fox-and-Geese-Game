// Course: CS 3642
// Student name: Colin Cavanaugh
// Student ID: 001055175
// Assignment #: 1
// Due Date: 9/16/2024
// Signature: Colin Cavanaugh

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FoxAndGeeseGame extends JFrame {

    private int EMPTY = 0;
    private int GOOSE = 1;
    private int FOX = 2;
    private int MAX_TURNS;

    // can set size of grid here
    private int SIZE = 6;

    private int[][] board = new int[SIZE][SIZE];
    private JButton[][] buttons = new JButton[SIZE][SIZE];

    // Coordinates of the selected goose
    private int selectedGooseX = -1;
    private int selectedGooseY = -1;

    private int turnCount = 0;

    public FoxAndGeeseGame() {
        showDifficultySelection();
        initializeBoard();
        initializeGUI();
    }

    // Show difficulty selection window
    private void showDifficultySelection() {
        String[] options = { "Easy (40 turns)", "Medium (30 turns)", "Hard (20 turns)" };
        int response = JOptionPane.showOptionDialog(null, "Select Difficulty Level:", "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // Set MAX_TURNS based on the difficulty selection
        switch (response) {
            case 0:
                // Easy
                MAX_TURNS = 40;
                break;
            case 1:
                // Medium
                MAX_TURNS = 30;
                break;
            case 2:
                // Hard
                MAX_TURNS = 20;
                break;
            default:
                // Default to Medium
                MAX_TURNS = 20;
        }
    }

    private void initializeBoard() {
        // Clear board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }

        // Place fox on board
        int foxX = (int) (Math.random() * SIZE);
        int foxY = (int) (Math.random() * SIZE);
        board[foxX][foxY] = FOX;

        // Place geese on board
        // Caan set number of geese here
        int numberOfGeese = 5;
        for (int i = 0; i < numberOfGeese; i++) {
            int gooseX, gooseY;
            do {
                gooseX = (int) (Math.random() * SIZE);
                gooseY = (int) (Math.random() * SIZE);
            } while (board[gooseX][gooseY] != EMPTY);
            board[gooseX][gooseY] = GOOSE;
        }
    }

    private void initializeGUI() {
        setTitle("Fox and Geese Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(SIZE, SIZE));

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                buttons[i][j].addActionListener(new GooseController(i, j));
                add(buttons[i][j]);
            }
        }

        updateBoard();
    }

    // Update the board with current game state
    private void updateBoard() {

        for (int i = 0; i < SIZE; i++) {

            for (int j = 0; j < SIZE; j++) {

                if (board[i][j] == GOOSE) {
                    buttons[i][j].setText("G"); // Represent geese with "G"
                    buttons[i][j].setBackground(new Color(173, 216, 230));
                } else if (board[i][j] == FOX) {
                    buttons[i][j].setText("F"); // Represent fox with "F"
                    buttons[i][j].setBackground(new Color(255, 99, 71));
                } else {
                    buttons[i][j].setText(""); // Empty cells
                    buttons[i][j].setBackground(new Color(235, 235, 235));
                }

                buttons[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                buttons[i][j].setVerticalAlignment(SwingConstants.CENTER);
            }
        }
    }

    // Controller for the geese
    private class GooseController implements ActionListener {
        private int x, y;

        public GooseController(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (board[x][y] == GOOSE) {
                selectedGooseX = x;
                selectedGooseY = y;
            } else if (board[x][y] == EMPTY && selectedGooseX != -1) {

                if (Math.abs(x - selectedGooseX) == 1 && Math.abs(y - selectedGooseY) == 1) {
                    board[selectedGooseX][selectedGooseY] = EMPTY;
                    board[x][y] = GOOSE;
                    selectedGooseX = -1;
                    selectedGooseY = -1;
                    updateBoard();

                    FoxAI foxAI = new FoxAI();
                    foxAI.makeMove();

                    if (checkWinOrLose()) {
                        return;
                    }
                }
            }
        }
    }

    // AI for controlling the fox - Model-Based Agent
    private class FoxAI {
        public void makeMove() {
            int foxX = -1, foxY = -1;

            // Find the current position of the fox
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == FOX) {
                        foxX = i;
                        foxY = j;
                        break;
                    }
                }
            }

            // Scan all potential moves and evaluate each one based on how "free" the
            // position is
            int[] dx = { -1, -1, -1, 0, 1, 1, 1, 0 };
            int[] dy = { -1, 0, 1, 1, 1, 0, -1, -1 };

            int bestMoveX = foxX;
            int bestMoveY = foxY;
            int maxFreeMoves = -1;

            for (int k = 0; k < 8; k++) {
                int newX = foxX + dx[k];
                int newY = foxY + dy[k];

                if (isValidMove(newX, newY)) {
                    int freeMoves = countFreeMoves(newX, newY);

                    if (freeMoves > maxFreeMoves) {
                        maxFreeMoves = freeMoves;
                        bestMoveX = newX;
                        bestMoveY = newY;
                    }
                }
            }

            // If a better move is found, the fox will move to that position
            if (bestMoveX != foxX || bestMoveY != foxY) {
                board[foxX][foxY] = EMPTY;
                board[bestMoveX][bestMoveY] = FOX;
                turnCount++;
                updateBoard();
            } else {
                // If no valid move is found, the fox is cornered
                JOptionPane.showMessageDialog(null, "The fox has been cornered! Geese win.");
                System.exit(0);
            }
        }

        // Count how many valid moves are available from a given position
        private int countFreeMoves(int x, int y) {
            int[] dx = { -1, -1, -1, 0, 1, 1, 1, 0 };
            int[] dy = { -1, 0, 1, 1, 1, 0, -1, -1 };
            int freeMoves = 0;

            // Check neighboring cells
            for (int k = 0; k < 8; k++) {
                int newX = x + dx[k];
                int newY = y + dy[k];
                if (isValidMove(newX, newY)) {
                    freeMoves++;
                }
            }
            return freeMoves;
        }

        // Validate if the fox can move to a given position (board bounds and empty
        // space check)
        private boolean isValidMove(int x, int y) {
            return x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == EMPTY;
        }
    }

    // Check if the fox or geese have won
    private boolean checkWinOrLose() {
        if (turnCount >= MAX_TURNS) {
            JOptionPane.showMessageDialog(null, "The fox survived for " + MAX_TURNS + " turns! Fox wins.");
            System.exit(0);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoxAndGeeseGame().setVisible(true));
    }
}