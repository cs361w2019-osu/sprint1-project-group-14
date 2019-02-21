package cs361.battleships.models;

public class ShipFactory {

    /**
     * Factory for creating ship objects.
     *
     * @param ship String name of ship
     * @return Ship ship object
     */
    public static Ship createShip(String ship) {
        if (ship.equals("DESTROYER")) return new Destroyer();
        else if (ship.equals("MINESWEEPER")) return new Minesweeper();
        else if (ship.equals("BATTLESHIP")) return new Battleship();
        else return null;
    }
}