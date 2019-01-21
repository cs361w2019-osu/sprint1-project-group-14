package cs361.battleships.models;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BoardTest {

    @Test
    public void testZeroLengthShip() {
        Board board = new Board();
        Ship s = new Ship();
        assertFalse(board.placeShip(s, 5, 'A', false));
    }

    @Test
    public void testStartOutOfBounds() {
        Board board = new Board();
        Ship s = new Ship("DESTROYER");
        assertFalse(board.placeShip(s, 11, 'A', false));
        assertFalse(board.placeShip(s, 1, 'J', false));
        assertTrue(board.placeShip(s, 1, 'A', false));
    }

    @Test
    public void testFullOutOfBounds() {
        Board board = new Board();
        Ship s = new Ship("DESTROYER");
        assertFalse(board.placeShip(s, 1, 'H', false));
        assertTrue(board.placeShip(s, 1, 'G', false));

        Board board2 = new Board();
        Ship s2 = new Ship("BATTLESHIP");
        assertFalse(board2.placeShip(s2, 7, 'A', true));
        assertTrue(board2.placeShip(s2, 6, 'A', true));
    }

    @Test
    public void testNoDuplicateShips() {
        Board board = new Board();
        Ship s = new Ship("MINESWEEPER");
        assertTrue(board.placeShip(s, 3, 'A', true));
        assertFalse(board.placeShip(s, 3, 'B', true));

        Ship s2 = new Ship("DESTROYER");
        assertTrue(board.placeShip(s2, 3, 'C', true));
        assertFalse(board.placeShip(s2, 3, 'D', true));

        Ship s3 = new Ship("BATTLESHIP");
        assertTrue(board.placeShip(s3, 3, 'E', true));
        assertFalse(board.placeShip(s3, 3, 'F', true));
    }

    @Test
    public void testCollision() {
        Board board = new Board();
        Ship s = new Ship("MINESWEEPER");
        assertTrue(board.placeShip(s, 3, 'A', true));
        Ship s2 = new Ship("DESTROYER");
        assertFalse(board.placeShip(s, 4, 'A', false));
        assertFalse(board.placeShip(s, 5, 'A', false));
    }
}
