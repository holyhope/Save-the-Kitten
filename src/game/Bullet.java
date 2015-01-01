package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * 
 * @author PERONNET Pierre
 * @author PICHOU Maxime
 */
public abstract class Bullet extends GameElement {
	/**
	 * Total amount of cat
	 */
	private static AtomicInteger nbCat = new AtomicInteger();
	/**
	 * Let name cat with integer if not specified
	 */
	private final String name = "Unamed bullet (" + nbCat.getAndIncrement()
			+ ")";
	/**
	 * True if bullet is not active.
	 */
	private boolean stopped = false;
	/**
	 * True if bullet fired.
	 */
	private boolean started = false;
	/**
	 * Filter contact
	 */
	private static final Filter filter;

	static {
		filter = new Filter();
		filter.categoryBits = 0x0002;
		filter.maskBits = 0xFFFF - filter.categoryBits;
	}

	protected Bullet(Body body, FixtureDef fixtureDef) {
		super(body);
		body.createFixture(fixtureDef).setFilterData(filter);
	}

	protected static BodyDef getBodyDef(Vec2 vec2, Vec2 velocity,
			float angularVelocity) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.bullet = true;
		bodyDef.active = false;
		bodyDef.fixedRotation = false;
		Objects.requireNonNull(vec2);
		bodyDef.position.set(vec2.x, vec2.y);
		Objects.requireNonNull(velocity);
		bodyDef.linearVelocity = new Vec2(velocity);
		Objects.requireNonNull(angularVelocity);
		bodyDef.angularVelocity = angularVelocity;
		return bodyDef;
	}

	/**
	 * Paint bullet on graphics
	 * 
	 * @param graphics
	 */
	@Override
	public void draw(Graphics2D graphics) {
		if (isStarted()) {
			graphics.setColor(Color.BLACK);
			super.draw(graphics);
		}
	}

	/**
	 * Get the constructor for a new Bullet of type classValue.
	 * 
	 * @param classValue
	 *            of the new Bullet to create.
	 * @return new method.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 */
	public static Method getConstructor(Class<? extends Bullet> classValue)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException {
		Method method = classValue.getDeclaredMethod("create", World.class,
				Vec2.class, Vec2.class, Float.class);
		if (Bullet.class.isInstance(method.getReturnType())) {
			throw new IllegalAccessException();
		}
		return method;
	}

	protected static FixtureDef getFixtureDef() {
		final FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setRadius(0.1f);
		dynamicBox.setAsBox(.00000001f, .00000001f);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0f;
		return fixtureDef;
	}

	@Override
	public String toString() {
		return name + " " + getPosition();
	}

	/**
	 * Called when cat contact another body.
	 * 
	 * @param body
	 */
	public void beginContact(Body body) {
		Object object = body.getUserData();
		if (object instanceof Goal) {
			((Goal) object).receive(this);
		}
	}

	/**
	 * Active the bullet
	 */
	public void start() {
		setActive(true);
		started = true;
	}

	/**
	 * Deactive the bullet.
	 */
	public void stop() {
		stopped = true;
		setActive(false);
	}

	/**
	 * Check if the bullet don't move
	 * 
	 * @return True if bullet is not active
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * Check if the bullet started at least once.
	 * 
	 * @return True if the bullet fired once.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Called when contact with another body is ended
	 * 
	 * @param body
	 *            colliding width that element.
	 */
	public abstract void endContact(Body body);
}
