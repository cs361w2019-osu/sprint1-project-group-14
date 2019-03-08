package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class DestroyerTest {

    @Test
    public void testShipCreation() {
        Ship s = ShipFactory.createShip("DESTROYER");;
        assertTrue(s.getShipName().equals("DESTROYER"));
        assertTrue(s.getLength() == 3);
    }

    @Test
    public void testInitializeShip() {
        Ship s = ShipFactory.createShip("DESTROYER");
        s.initialize(5, 'B', true, 1);

        List<Square> squareList2 = new ArrayList<>();
        squareList2.add(new Square(5, 'B'));
        squareList2.add(new Square(6, 'B'));
        squareList2.add(new Square(7, 'B'));

        assertTrue(s.getOccupiedSquares().equals(squareList2));
    }

    @Test
    public void testSinkShip() {
        Ship s = ShipFactory.createShip("DESTROYER");
        s.initialize(5, 'B', true, 1);

        assertEquals(false, s.registerAttack(new Square(4, 'B'), Weapon.BOMB));
        assertEquals(true, s.registerAttack(new Square(5, 'B'), Weapon.BOMB));
        assertEquals(false, s.registerAttack(new Square(6, 'B'), Weapon.BOMB));

        assertEquals(false, s.isSunk());
        assertEquals(true, s.registerAttack(new Square(6, 'B'), Weapon.BOMB));
        assertEquals(true, s.isSunk());
    }
}