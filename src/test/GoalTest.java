package test;

import static org.junit.Assert.*;
import game.Bullet;
import game.Goal;

import java.awt.Point;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.Test;

public class GoalTest {
	@Test
	public void testCreate() {
		Goal.create(new World(new Vec2(0, 0)), new Point(2, 2));
	}

	@Test(expected = NullPointerException.class)
	public void testCreatePositionNull() {
		Goal.create(new World(new Vec2(0, 0)), null);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateWorldNull() {
		Goal.create(null, new Point(2, 2));
	}

	@Test
	public void testReceive() {
		World world = new World(new Vec2(0, 0));
		Goal goal = Goal.create(world, new Point(2, 2));
		Bullet bullet = Bullet.create(world, new Point(2, 2), new Vec2(0, 0));
		assertTrue(goal.receive(bullet));
		assertFalse(goal.receive(bullet));
	}

	@Test(expected = NullPointerException.class)
	public void testReceiveNull() {
		World world = new World(new Vec2(0, 0));
		Goal goal = Goal.create(world, new Point(2, 2));
		goal.receive(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReceiveParallelWorld() {
		Goal goal = Goal.create(new World(new Vec2(0, 0)), new Point(2, 2));
		goal.receive(Bullet.create(new World(new Vec2(0, 0)), new Point(2, 2),
				new Vec2(0, 0)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReceiveInvalidCat() {
		Goal goal = Goal.create(new World(new Vec2(0, 0)), new Point(2, 2));
		goal.receive(Bullet.create(new World(new Vec2(0, 0)), new Point(2, 2),
				new Vec2(0, 0)));
	}

	@Test
	public void testIsFull() {
		World world = new World(new Vec2(0, 0));
		Goal goal = Goal.create(world, new Point(2, 2));
		assertFalse(goal.isFull());
		goal.receive(Bullet.create(world, new Point(2, 2), new Vec2(0, 0)));
		assertTrue(goal.isFull());
	}

}
