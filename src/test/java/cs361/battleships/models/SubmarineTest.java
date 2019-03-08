package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class SubmarineTest {

    @Test
    public void testShipCreation() {
        Ship s = ShipFactory.createShip("SUBMARINE");;
        assertTrue(s.getShipName().equals("SUBMARINE"));
        assertTrue(s.getLength() == 4);
        assertTrue(s.getWidth() == 2);
    }

    @Test
    public void testInitializeShip() {
        Ship s = ShipFactory.createShip("SUBMARINE");
        s.initialize(5, 'B', true, 0);

        List<Square> squareList2 = new ArrayList<>();
        squareList2.add(new Square(5, 'B'));
        squareList2.add(new Square(6, 'B'));
        squareList2.add(new Square(7, 'B'));
        squareList2.add(new Square(8, 'B'));
        squareList2.add(new Square(7, 'C'));

        assertTrue(s.getOccupiedSquares().equals(squareList2));
    }

    @Test
    public void testSinkShip() {
        Ship s = ShipFactory.createShip("SUBMARINE");
        s.initialize(5, 'B', true, 0);

        // Change when new weapon implemented
        assertEquals(false, s.registerAttack(new Square(4, 'B'), Weapon.BOMB));
        assertEquals(false, s.registerAttack(new Square(7, 'C'), Weapon.BOMB));
        assertEquals(true, s.registerAttack(new Square(7, 'C'), Weapon.LASER));
        assertEquals(false, s.registerAttack(new Square(8, 'B'), Weapon.LASER));

        assertEquals(false, s.isSunk());
        assertEquals(true, s.registerAttack(new Square(8, 'B'), Weapon.LASER));
        assertEquals(true, s.isSunk());

        // When placed on surface, bombs work
        Ship s2 = ShipFactory.createShip("SUBMARINE");
        s2.initialize(5, 'B', true, 1);
        assertEquals(true, s2.registerAttack(new Square(7, 'C'), Weapon.BOMB));
    }
}