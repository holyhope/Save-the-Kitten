package game;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

public abstract class Bullet extends GameElement {
	/**
	 * Let name cat with integer if not specified
	 */
	private static AtomicInteger nbCat = new AtomicInteger();
	private final String name = "Unamed cat (" + nbCat.getAndIncrement() + ")";
	private boolean stopped = false;
	private final FixtureDef fixtureDef;
	private Fixture fixture;
	private static final Filter filter;

	static {
		filter = new Filter();
		filter.categoryBits = 0x0002;
		filter.maskBits = 0xFFFF - filter.categoryBits;
	}

	protected Bullet(Body body, FixtureDef fixtureDef) {
		super(body);
		this.fixtureDef = fixtureDef;
	}

	protected static BodyDef getBodyDef(Vec2 vec2, Vec2 velocity,
			float angularVelocity) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.bullet = true;
		bodyDef.active = false;
		Objects.requireNonNull(vec2);
		bodyDef.position.set(vec2.x, vec2.y);
		Objects.requireNonNull(velocity);
		bodyDef.linearVelocity = new Vec2(velocity.x, velocity.y);
		Objects.requireNonNull(angularVelocity);
		bodyDef.angularVelocity = angularVelocity;
		return bodyDef;
	}

	protected static FixtureDef getFixtureDef() {
		final FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		return fixtureDef;
	}

	@Override
	public String toString() {
		return name;
	}

	public void beginContact(Body body) {
		Object object = body.getUserData();
		if (object instanceof Goal) {
			((Goal) object).receive(this);
		}
	}

	public void start() {
		do {
			fixture = getBody().createFixture(fixtureDef);
		} while (fixture == null);
		fixture.setFilterData(filter);
		stopped = false;
		getBody().setActive(true);
	}

	public void stop() {
		getBody().setActive(false);
		getBody().destroyFixture(fixture);
		stopped = true;
	}

	public boolean isStopped() {
		return stopped;
	}

	public abstract void endContact(Body body);
}
