package game;

import java.awt.Graphics2D;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public final class Cat extends Bullet {
	/**
	 * Create a cat
	 * 
	 * @param world
	 *            of the cat
	 * @param position
	 *            of the cat
	 * @param velocity
	 *            of the cat
	 * @param angularVelocity
	 *            of the cat. Make it roll !
	 * @return new Cat
	 */
	public static Cat create(World world, Vec2 position, Vec2 velocity,
			float angularVelocity) {
		Body body;
		BodyDef bodyDef = getBodyDef(position, velocity, angularVelocity);
		do {
			body = world.createBody(bodyDef);
		} while (body == null);
		return new Cat(body);

	}

	/**
	 * Create a cat
	 * 
	 * @param world
	 *            of the cat
	 * @param position
	 *            of the cat
	 * @param velocity
	 *            of the cat
	 * @return new Cat
	 */
	public static Cat create(World world, Vec2 position, Vec2 velocity) {
		return create(world, position, velocity, 0);
	}

	private Cat(Body body) {
		super(body, getFixtureDef());
	}

	/**
	 * Stop the cat.
	 * 
	 * @param body
	 *            of the other element.
	 */
	@Override
	public void beginContact(Body body) {
		super.beginContact(body);
		stop();
	}

	@Override
	public void endContact(Body body) {
	}

	/**
	 * Paint cat on graphics
	 * 
	 * @param graphics
	 */
	@Override
	public void draw(Graphics2D graphics) {
		Vec2 position = getPosition();
		graphics.fillRect(Math.round(position.x), Math.round(position.y), 10,
				10);
	}
}
