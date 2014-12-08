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
		new Launcher(null, 4);
	}

	@Test
	public void testLauncher() {
		new Launcher(new Point(50, 25), 4);
	}

	@Test
	public void testGetPosition() {
		Launcher launcher;
		launcher = new Launcher(new Point(5, 7), 0);
		assertEquals(new Point(5, 7), launcher.getPosition());
		launcher = new Launcher(new Point(0, -10), 0);
		assertEquals(new Point(0, -10), launcher.getPosition());
	}

	@Test
	public void testLaunch() {
		Launcher launcher;
		World world = new World(new Vec2(0,0));

		launcher = new Launcher(new Point(5, 7), 3, new Vec2(1,0));
		launcher.launch(world);
		assertEquals(3, world.getBodyCount());

		launcher = new Launcher(new Point(5, 7), 0);
		launcher.launch(world);
		assertEquals(4, world.getBodyCount());

		world = new World(new Vec2(0,0));
		launcher.launch(world);
		assertEquals(1, world.getBodyCount());
	}
}
