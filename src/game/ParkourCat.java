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
	 * Speed of the cat when climbing to walls.
	 */
	private static final Vec2 SPEED_CLIMB = new Vec2(0f, .015f);
	/**
	 * Speed of the cat after climbing.
	 */
	private static final Vec2 SPEED_JUMP = new Vec2(.015f, .015f);
	/**
	 * Number of wall climbed by the cat
	 */
	private AtomicInteger parkoured = new AtomicInteger(0);
	/**
	 * Number of wall that the cat is able to climb
	 */
	private static final int MAX_PARKOUR = 1;

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
		if (parkoured.getAndIncrement() < MAX_PARKOUR) {
			getBody().setLinearVelocity(SPEED_CLIMB);
		} else {
			stop();
		}
	}

	@Override
	public void endContact(Body body) {
		getBody().setLinearVelocity(SPEED_JUMP);
	}
}
