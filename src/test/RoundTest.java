package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import game.Launcher;
import game.Round;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.Test;

/**
 * 
 * @author PERONNET Pierre
 * @author PICHOU Maxime
 */
public class RoundTest {
	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionWidthNegative() {
		Round.create(new World(new Vec2(0, 0)), -5, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundDimensionHeightNull() {
		Round.create(new World(new Vec2(0, 0)), 5, 0);
	}

	@Test(expected = NullPointerException.class)
	public void testRoundWorldNull() {
		Round.create(null, 15, 15);
	}

	@Test(expected = IllegalStateException.class)
	public void testRoundDifferentWorld() {
		Round round = Round.create(new World(new Vec2(0, 0)), 15, 15);
		round.add(Launcher.create(new World(new Vec2(0, 0)), new Vec2(5, 5),
				new Vec2(20, 0)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherNegative() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 15, 15);
		round.add(Launcher.create(world, new Vec2(-5, 5), new Vec2(20, 0)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoundLauncherOut() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 15, 15);
		round.add(Launcher.create(world, new Vec2(300, 50), new Vec2(25, 0)));
	}

	@Test(expected = IllegalStateException.class)
	public void testRoundTwice() {
		World world = new World(new Vec2(0, 0));
		Round.create(world, 15, 15);
		Round.create(world, 15, 15);
	}

	@Test
	public void testRound() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 15, 15);
		round.add(Launcher.create(world, new Vec2(5, 5), new Vec2(20, 0)));
		world = new World(new Vec2(0, 0));
		round = Round.create(world, 35, 20);
		round.add(Launcher.create(world, new Vec2(5, 5), new Vec2(20, 0)));
	}

	@Test
	public void testIsInBoard() {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 500, 500);
		assertTrue(round.isInArea(new Vec2(25, 250)));
		assertTrue(round.isInArea(new Vec2(0, 0)));
		assertTrue(round.isInArea(new Vec2(499, 499)));
		assertFalse(round.isInArea(new Vec2(500, 250)));
		assertFalse(round.isInArea(new Vec2(25, -3)));
	}

	@Test(expected = NullPointerException.class)
	public void testIsInBoardNull() {
		Round.create(new World(new Vec2(0, 0)), 15, 15).isInArea(null);
	}
}
