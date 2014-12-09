package test;

import static org.junit.Assert.*;
import game.Launcher;

import java.awt.Point;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.Test;

public class LauncherTest {
	@Test(expected = NullPointerException.class)
	public void testLauncherPositionNull() {
		Launcher.create(new World(new Vec2(0, 0)), null, 4);
	}

	@Test(expected = NullPointerException.class)
	public void testLauncherWorldNull() {
		Launcher.create(null, new Point(50, 25), 4);
	}

	@Test
	public void testLauncher() {
		World world = new World(new Vec2(0, 0));
		Launcher.create(world, new Point(50, 25), 4);
		assertEquals(1, world.getBodyCount());
		Launcher.create(world, new Point(50, 25), 4);
		assertEquals(2, world.getBodyCount());
	}

	@Test
	public void testLaunch() {
		Launcher launcher;
		World world = new World(new Vec2(0, 0));

		launcher = Launcher.create(world, new Point(5, 7), 3, new Vec2(1, 0));
		launcher.launch();
		assertEquals(4, world.getBodyCount());

		launcher = Launcher.create(world, new Point(5, 7), 0);
		launcher.launch();
		assertEquals(6, world.getBodyCount());

	}
}
