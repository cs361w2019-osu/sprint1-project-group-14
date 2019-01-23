package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShipTest {

    @Test
    public void testShipCreation() {
        Ship s = new Ship("MINESWEEPER");
        assertTrue(s.getShipName().equals("MINESWEEPER"));
        assertTrue(s.getLength() == 2);

        Ship s2 = new Ship("DESTROYER");
        assertTrue(s2.getShipName().equals("DESTROYER"));
        assertTrue(s2.getLength() == 3);

        Ship s3 = new Ship("BATTLESHIP");
        assertTrue(s3.getShipName().equals("BATTLESHIP"));
        assertTrue(s3.getLength() == 4);

        Ship s4 = new Ship();
        assertTrue(s4.getShipName() == null);
        assertTrue(s4.getLength() == 0);

        Ship s5 = new Ship("FAKE NAME");
        assertTrue(s5.getShipName().equals("FAKE NAME"));
        assertTrue(s5.getLength() == 0);
    }

    @Test
    public void testUpdateSquares() {
        Ship s = new Ship("MINESWEEPER");
        s.updateOccupiedSquares(5, 'B', false);

        List<Square> squareList = new ArrayList<>();
        squareList.add(new Square(5, 'B'));
        squareList.add(new Square(5, 'C'));

        assertTrue(s.getOccupiedSquares().equals(squareList));

        Ship s2 = new Ship("BATTLESHIP");
        s2.updateOccupiedSquares(5, 'B', true);

        List<Square> squareList2 = new ArrayList<>();
        squareList2.add(new Square(5, 'B'));
        squareList2.add(new Square(6, 'B'));
        squareList2.add(new Square(7, 'B'));
        squareList2.add(new Square(8, 'B'));

        assertTrue(s2.getOccupiedSquares().equals(squareList2));
    }
}