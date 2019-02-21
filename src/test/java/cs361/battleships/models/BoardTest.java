package cs361.battleships.models;

import org.junit.Test;

import static org.junit.Assert.*;


public class BoardTest {

    /**
    @Test
    public void testZeroLengthShip() {
        Board board = new Board();
        Ship s = new Ship();
        assertFalse(board.placeShip(s, 5, 'A', false));
    }
     */

    @Test
    public void testStartOutOfBounds() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("DESTROYER");
        assertFalse(board.placeShip(s, 11, 'A', false));
        assertFalse(board.placeShip(s, 1, 'J', false));
        assertTrue(board.placeShip(s, 1, 'A', false));
    }

    @Test
    public void testFullOutOfBounds() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("DESTROYER");
        assertFalse(board.placeShip(s, 1, 'I', false));
        assertTrue(board.placeShip(s, 1, 'H', false));

        Board board2 = new Board();
        Ship s2 = ShipFactory.createShip("BATTLESHIP");
        assertFalse(board2.placeShip(s2, 8, 'A', true));
        assertTrue(board2.placeShip(s2, 7, 'A', true));
    }

    @Test
    public void testNoDuplicateShips() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("MINESWEEPER");
        assertTrue(board.placeShip(s, 3, 'A', true));
        assertFalse(board.placeShip(s, 3, 'B', true));

        Ship s2 = ShipFactory.createShip("DESTROYER");
        assertTrue(board.placeShip(s2, 3, 'C', true));
        assertFalse(board.placeShip(s2, 3, 'D', true));

        Ship s3 = ShipFactory.createShip("BATTLESHIP");
        assertTrue(board.placeShip(s3, 3, 'E', true));
        assertFalse(board.placeShip(s3, 3, 'F', true));
    }

    @Test
    public void testCollision() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("MINESWEEPER");
        assertTrue(board.placeShip(s, 3, 'A', true));
        Ship s2 = ShipFactory.createShip("DESTROYER");
        assertFalse(board.placeShip(s2, 4, 'A', false));
        assertFalse(board.placeShip(s, 5, 'A', false));
    }

    @Test
    public void testOutOfBoundsAttack() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("MINESWEEPER");
        board.placeShip(s,3,'A',true);

        assertSame(board.attack(12,'A').getResult(), AtackStatus.INVALID);
        assertSame(board.attack(2,'L').getResult(), AtackStatus.INVALID);
    }

    @Test
    public void testAttackCollision() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("MINESWEEPER");
        board.placeShip(s, 3, 'C', true);
        assertSame(board.attack(4, 'C').getResult(), AtackStatus.HIT);
        assertNotSame(board.attack(4,'C').getResult(), AtackStatus.INVALID);
    }

    @Test
    public void testMissedAttack() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("MINESWEEPER");
        board.placeShip(s,4,'C',true);

        assertSame(board.attack(1,'A').getResult(), AtackStatus.MISS );
        assertSame(board.attack(2,'D').getResult(), AtackStatus.MISS );
    }

    @Test
    public void testShipSunk() {
        Board board = new Board();
        Ship d = ShipFactory.createShip("DESTROYER");
        Ship s = ShipFactory.createShip("MINESWEEPER");
        board.placeShip(s, 3, 'A', true);
        board.placeShip(d, 4, 'D', false);

        assertSame(board.attack(3, 'A').getResult(), AtackStatus.MISS);
        assertSame(board.attack(3, 'A').getResult(), AtackStatus.SUNK);
    }

    @Test
    public void testGameSurrender() {
        Board board = new Board();
        Ship d = ShipFactory.createShip("DESTROYER");
        Ship s = ShipFactory.createShip("MINESWEEPER");
        board.placeShip(d, 4, 'D', false);
        board.placeShip(s, 2, 'C', true);

        assertSame(board.attack(4, 'D').getResult(), AtackStatus.HIT);
        assertSame(board.attack(4, 'E').getResult(), AtackStatus.MISS);
        assertSame(board.attack(4, 'E').getResult(), AtackStatus.SUNK);
        assertTrue(d.isSunk());

        assertSame(board.attack(2, 'C').getResult(), AtackStatus.MISS);
        assertSame(board.attack(2, 'C').getResult(), AtackStatus.SURRENDER);
    }

    @Test
    public void testNoSonarCharges() {
        Board board = new Board();
        assertSame(board.sonar(4,'D').getResult(), AtackStatus.INVALID);
    }
    @Test
    public void testSonarCharges() {
        Board board = new Board();
        Ship s = ShipFactory.createShip("MINESWEEPER");
        board.placeShip(s, 4, 'D', false);
        board.attack(4,'D');
        board.attack(4,'D');
        assertSame(board.sonar(4,'D').getResult(), AtackStatus.SONAR);
    }
}
