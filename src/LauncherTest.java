import static org.junit.Assert.*;

import org.junit.Test;

public class LauncherTest {

	@Test(expected = IllegalArgumentException.class)
	public void testLauncherNbCatNull() {
		new Launcher(0, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLauncherPosXNegative() {
		new Launcher(-5, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLauncherPosYNegative() {
		new Launcher(0, -5, 0);
	}

	@Test
	public void testLauncher() {
		new Launcher(500, 25, 4);
	}

	@Test
	public void testCreateBody() {
		new Launcher(50, 50, 2);
	}

	@Test
	public void testGetWaitTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNbCat() {
		fail("Not yet implemented");
	}

}
