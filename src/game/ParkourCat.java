package game;

import java.util.concurrent.atomic.AtomicInteger;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * 
 * @author PERONNET Pierre
 * @author PICHOU Maxime
 */
public final class ParkourCat extends Bullet {
	/**
	 * Number of wall climbed by the cat
	 */
	private AtomicInteger parkoured = new AtomicInteger(0);
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
	public void beginContact(Body body) {
		super.beginContact(body);
		// TODO Test
		if (parkoured.getAndIncrement() < maxParkour) {
			setLinearVelocity(new Vec2(0f, .01f));
		} else {
			stop();
		}
	}

	@Override
	public void endContact(Body body) {
		setLinearVelocity(new Vec2(.01f, .01f));
	}
}
