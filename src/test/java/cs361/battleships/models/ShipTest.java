package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void testShipCreation() {
        Ship s = ShipFactory.createShip("MINESWEEPER");
        assertEquals("MINESWEEPER", s.getShipName());
        assertEquals(2, s.getLength());

        Ship s2 = ShipFactory.createShip("DESTROYER");
        assertEquals("DESTROYER", s2.getShipName());
        assertEquals(3, s2.getLength());

        Ship s3 = ShipFactory.createShip("BATTLESHIP");
        assertEquals("BATTLESHIP", s3.getShipName());
        assertEquals(4, s3.getLength());

        Ship s4 = ShipFactory.createShip("");
        assertNull(s4);

        Ship s5 = ShipFactory.createShip("FAKE NAME");
        assertNull(s5);
    }

    @Test
    public void testUpdateSquares() {
        Ship s = ShipFactory.createShip("MINESWEEPER");
        s.initialize(5, 'B', false);

        List<Square> squareList = new ArrayList<>();
        squareList.add(new Square(5, 'B'));
        squareList.add(new Square(5, 'C'));

        assertEquals(s.getOccupiedSquares(), squareList);

        Ship s2 = ShipFactory.createShip("BATTLESHIP");
        s2.initialize(5, 'B', true);

        List<Square> squareList2 = new ArrayList<>();
        squareList2.add(new Square(5, 'B'));
        squareList2.add(new Square(6, 'B'));
        squareList2.add(new Square(7, 'B'));
        squareList2.add(new Square(8, 'B'));

        assertEquals(s2.getOccupiedSquares(), squareList2);
    }
}