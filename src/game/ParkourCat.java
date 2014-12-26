package game;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JWindow;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public final class ParkourCat extends Bullet {
	private AtomicInteger parkoured = new AtomicInteger();
	private static final int maxParkour = 1;

	public static ParkourCat create(World world, Vec2 position, Vec2 velocity,
			float angularVelocity) {
		Body body;
		BodyDef bodyDef = getBodyDef(position, velocity, angularVelocity);
		do {
			body = world.createBody(bodyDef);
		} while (body == null);
		return new ParkourCat(body);
	}

	public static ParkourCat create(World world, Vec2 position, Vec2 velocity) {
		return create(world, position, velocity, 0);
	}

	private ParkourCat(Body body) {
		super(body, getFixtureDef());
	}

	@Override
	public void draw(JWindow window) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beginContact(Body body) {
		super.beginContact(body);
		// TODO Test
		if ( parkoured.getAndIncrement() < maxParkour)  {
			getBody().setLinearVelocity(new Vec2(0, 0.1f));
		} else {
			stop();
		}
	}

	@Override
	public void endContact(Body body) {
		// TODO make the cat 'jump'
	}
}
