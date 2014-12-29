package game;

import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public final class ParkourCat extends Bullet {
	/**
	 * Number of wall climbed by the cat
	 */
	private AtomicInteger parkoured = new AtomicInteger();
	/**
	 * Number of wall that the cat is able to climb
	 */
	private static final int maxParkour = 1;

	/**
	 * Create a parkourCat in world at position with some velocity
	 * 
	 * @param world
	 *            of the cat
	 * @param position
	 *            of the cat
	 * @param velocity
	 *            of the cat
	 * @param angularVelocity
	 *            of the cat. Make it roll !
	 * @return new ParkourCat
	 */
	public static ParkourCat create(World world, Vec2 position, Vec2 velocity,
			Float angularVelocity) {
		Body body;
		BodyDef bodyDef = getBodyDef(position, velocity, angularVelocity);
		do {
			body = world.createBody(bodyDef);
		} while (body == null);
		return new ParkourCat(body);
	}

	private ParkourCat(Body body) {
		super(body, getFixtureDef());
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beginContact(Body body) {
		super.beginContact(body);
		// TODO Test
		if (parkoured.getAndIncrement() < maxParkour) {
			setLinearVelocity(new Vec2(0, 0.1f));
		} else {
			stop();
		}
	}

	@Override
	public void endContact(Body body) {
		// TODO make the cat 'jump'
	}
}
