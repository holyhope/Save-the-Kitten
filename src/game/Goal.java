package game;

import java.awt.Graphics2D;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Goal extends GameElement {
	private final int nbSlot = 1;
	private final AtomicInteger nbCat = new AtomicInteger();
	private final LinkedHashSet<Bullet> bullets = new LinkedHashSet<>();

	private Goal(Body body) {
		super(body);
		body.createFixture(getFixtureDef());
	}

	public static Goal create(World world, Vec2 vec2) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(vec2.x, vec2.y);
		bodyDef.type = BodyType.STATIC;
		bodyDef.awake = true;
		Body body = world.createBody(bodyDef);
		return new Goal(body);
	}

	/**
	 * Draw goal on graphics
	 */
	@Override
	public void draw(Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	/**
	 * Receive a bullet.
	 * 
	 * @param bullet
	 *            caught by the goal
	 * @return False if cat cannot be received by the goal
	 */
	public boolean receive(Bullet bullet) {
		Objects.requireNonNull(bullet);
		if (!bullet.getWorld().equals(getWorld())) {
			throw new IllegalArgumentException(
					"Bullet and Goal are not in the same World.");
		}
		if (nbCat.getAndIncrement() >= nbSlot || !bullets.add(bullet)) {
			nbCat.decrementAndGet();
			return false;
		}
		bullet.stop();
		return true;
	}

	/**
	 * Check if goal can receive more bullets
	 * 
	 * @return True if goal is full
	 */
	public boolean isFull() {
		return nbSlot == nbCat.get();
	}

	private static FixtureDef getFixtureDef() {
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		return fixtureDef;
	}
}
