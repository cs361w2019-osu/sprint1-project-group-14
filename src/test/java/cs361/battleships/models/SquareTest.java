package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SquareTest {

    @Test
    public void testEquality() {
        Square sq = new Square(5, 'A');
        Square sq2 = new Square(5, 'A');
        Square sq3 = new Square(5,'B');
        Square sq4 = new Square(6,'A');

        assertTrue(sq.equals(sq2));
        assertFalse(sq.equals(sq3));
        assertFalse(sq.equals(sq4));
    }

    @Test
    public void testContainment() {
        List<Square> squareList = new ArrayList<>();
        squareList.add(new Square(5, 'A'));
        squareList.add(new Square(6, 'B'));
        squareList.add(new Square(7, 'C'));

        assertTrue(squareList.contains(new Square(5, 'A')));
        assertFalse(squareList.contains(new Square(6, 'A')));
    }
}