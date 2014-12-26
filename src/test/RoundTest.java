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
		Round.create(new World(new Vec2(0, 0)), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionWidthNegative() {
		Round.create(new World(new Vec2(0, 0)), new Dimension(-5, 5));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionHeightNull() {
		Round.create(new World(new Vec2(0, 0)), new Dimension(5, 0));
	}

	@Test(expected = NullPointerException.class)
	public void testRoundWorldNull() {
		Round.create(null, new Dimension(15, 15));
	}

	@Test(expected = IllegalStateException.class)
	public void testRoundDifferentWorld() {
		Round round = Round.create(new World(new Vec2(0, 0)), new Dimension(15,
				15));
		round.add(Launcher
				.create(new World(new Vec2(0, 0)), new Vec2(5, 5), 20));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherNegative() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, new Dimension(15, 15));
		round.add(Launcher.create(world, new Vec2(-5, 5), 20));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherOut() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, new Dimension(15, 15));
		round.add(Launcher.create(world, new Vec2(300, 50), 25));
	}

	@Test(expected = IllegalStateException.class)
	public void testRoundTwice() {
		World world = new World(new Vec2(0, 0));
		Round.create(world, new Dimension(15, 15));
		Round.create(world, new Dimension(15, 15));
	}

	@Test
	public void testRound() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, new Dimension(15, 15));
		round.add(Launcher.create(world, new Vec2(5, 5), 20));
		world = new World(new Vec2(0, 0));
		round = Round.create(world, new Dimension(35, 20));
		round.add(Launcher.create(world, new Vec2(5, 5), 20));
	}

	@Test
	public void testIsInBoard() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, new Dimension(500, 500));
		assertTrue(round.isInBoard(new Vec2(25, 250)));
		assertTrue(round.isInBoard(new Vec2(0, 0)));
		assertTrue(round.isInBoard(new Vec2(499, 499)));
		assertFalse(round.isInBoard(new Vec2(500, 250)));
		assertFalse(round.isInBoard(new Vec2(25, -3)));
	}

	@Test(expected = NullPointerException.class)
	public void testIsInBoardNull() {
		Round.create(new World(new Vec2(0, 0)), new Dimension(15, 15))
				.isInBoard(null);
	}
}
