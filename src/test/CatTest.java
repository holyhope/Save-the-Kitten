package test;

import game.Cat;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.Test;

/**
 * 
 * @author PERONNET Pierre
 * @author PICHOU Maxime
 */
public class CatTest {

	@Test
	public void testCreate() {
		Cat.create(new World(new Vec2(0,0)), new Vec2(0, 0), new Vec2(0, 0), 0f);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateWorldNull() {
		Cat.create(null, new Vec2(0, 0), new Vec2(0, 0), 1f);
	}

	@Test(expected = NullPointerException.class)
	public void testCreatePointNull() {
		Cat.create(new World(new Vec2(0,0)), null, new Vec2(0, 0), 1f);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateVec2Null() {
		Cat.create(new World(new Vec2(0,0)), new Vec2(0, 0), null, 1f);
	}

}
