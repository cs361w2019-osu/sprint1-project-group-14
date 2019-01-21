package cs361.battleships.models;

import org.junit.Test;

import static org.junit.Assert.assertFalse;


public class AIPlacementTest {
    @Test
    public void testInvalidPosition() {
        Game g = new Game();
        for (int i = 0; i < 1000; i++) {
            assertFalse(g.randCol()>'k');
            assertFalse(g.randRow()>10);
            assertFalse(g.randRow()< 0);
        }
    }
}
