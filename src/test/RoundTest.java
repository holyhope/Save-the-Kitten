package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import game.Launcher;
import game.Round;

import java.awt.Dimension;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.Test;

public class RoundTest {
	@Test(expected = NullPointerException.class)
	public void testRoundDimensionNull() {
		Round.create(null,
				Launcher.create(new World(new Vec2(0, 0)), new Vec2(5, 5), 20));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionWidthNegative() {
		Round.create(new Dimension(-5, 5),
				Launcher.create(new World(new Vec2(0, 0)), new Vec2(5, 5), 20));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionHeightNull() {
		Round.create(new Dimension(5, 0),
				Launcher.create(new World(new Vec2(0, 0)), new Vec2(5, 5), 20));
	}

	@Test(expected = NullPointerException.class)
	public void testRoundLauncherNull() {
		Round.create(new Dimension(15, 15), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherNegative() {
		Round.create(new Dimension(15, 15), Launcher.create(new World(new Vec2(
				0, 0)), new Vec2(-5, 5), 20));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherOut() {
		Round.create(new Dimension(15, 15), Launcher.create(new World(new Vec2(
				0, 0)), new Vec2(300, 50), 25));
	}

	@Test
	public void testRound() {
		Round.create(new Dimension(15, 15),
				Launcher.create(new World(new Vec2(0, 0)), new Vec2(5, 5), 20));
		Round.create(new Dimension(35, 20), Launcher.create(new World(new Vec2(
				0, 0)), new Vec2(25, 15), 20));
	}

	@Test
	public void testIsInBoard() {
		Round round = Round.create(new Dimension(500, 500), Launcher.create(
				new World(new Vec2(0, 0)), new Vec2(25, 25), 20));
		assertTrue(round.isInBoard(new Vec2(25, 250)));
		assertTrue(round.isInBoard(new Vec2(0, 0)));
		assertTrue(round.isInBoard(new Vec2(499, 499)));
		assertFalse(round.isInBoard(new Vec2(500, 250)));
		assertFalse(round.isInBoard(new Vec2(25, -3)));
	}

	@Test(expected = NullPointerException.class)
	public void testIsInBoardPointNull() {
		Round round = Round
				.create(new Dimension(15, 15), Launcher.create(new World(
						new Vec2(0, 0)), new Vec2(5, 5), 20));
		Vec2 point = null;
		round.isInBoard(point);
	}

	@Test(expected = NullPointerException.class)
	public void testIsInBoardVec2Null() {
		Round round = Round
				.create(new Dimension(15, 15), Launcher.create(new World(
						new Vec2(0, 0)), new Vec2(5, 5), 20));
		Vec2 vec2 = null;
		round.isInBoard(vec2);
	}
}
