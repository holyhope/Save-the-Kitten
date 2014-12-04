package test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import game.GameElement;
import game.Goal;
import game.Launcher;
import game.Round;

import java.awt.Point;

import org.junit.Test;

public class RoundTest {

	@Test(expected = IllegalArgumentException.class)
	public void testRoundNegative() {
		Round.construct(200, -100, new Launcher(0, 0, 25));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionNull() {
		Round.construct(0, 200, new Launcher(0, 0, 25));
	}

	@Test(expected = NullPointerException.class)
	public void testRoundLauncherNull() {
		Round.construct(200, 200, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherNegative() {
		Round.construct(200, 200, new Launcher(-50, 20, 25));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherOut() {
		Round.construct(200, 200, new Launcher(300, 50, 25));
	}

	@Test
	public void testRound() {
		Round.construct(500, 200, new Launcher(20, 20, 30));
		Round.construct(250, 300, new Launcher(25, 15, 20));
	}

	@Test
	public void testIsInBoard() {
		Round round = Round.construct(500, 500, new Launcher(20, 20, 25));
		assertTrue(round.isInBoard(new Point(25, 250)));
		assertTrue(round.isInBoard(new Point(0, 0)));
		assertTrue(round.isInBoard(new Point(499, 499)));
		assertFalse(round.isInBoard(new Point(500, 250)));
		assertFalse(round.isInBoard(new Point(25, -3)));
	}

	@Test(expected = NullPointerException.class)
	public void testIsInBoardNull() {
		Round round = Round.construct(500, 500, new Launcher(20, 20, 25));
		round.isInBoard(null);
	}

	@Test
	public void testAddElementGameElement() {
		fail("Not yet implemented");
	}

	@Test(expected = NullPointerException.class)
	public void testAddGameElementNull() {
		Round round = Round.construct(500, 500, new Launcher(20, 20, 25));
		GameElement element = null;
		round.add(element, new Point(2, 2));
	}

	@Test
	public void testAddGoal() {
		Round round = Round.construct(500, 500, new Launcher(20, 20, 50));
		Goal element = new Goal();
		round.add(element, new Point(2, 2));
	}

	@Test(expected = NullPointerException.class)
	public void testAddGoalNull() {
		Round round = Round.construct(500, 500, new Launcher(20, 20, 20));
		Goal element = null;
		round.add(element, new Point(2, 2));
	}
}
