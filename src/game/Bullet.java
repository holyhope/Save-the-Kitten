package game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

public abstract class Bullet extends GameElement {
	/**
	 * Total amount of cat
	 */
	private static AtomicInteger nbCat = new AtomicInteger();
	/**
	 * Let name cat with integer if not specified
	 */
	private final String name = "Unamed cat (" + nbCat.getAndIncrement() + ")";
	/**
	 * True if bullet is not active
	 */
	private boolean stopped = false;
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
		Objects.requireNonNull(vec2);
		bodyDef.position.set(vec2.x, vec2.y);
		Objects.requireNonNull(velocity);
		bodyDef.linearVelocity = new Vec2(velocity);
		Objects.requireNonNull(angularVelocity);
		bodyDef.angularVelocity = angularVelocity;
		return bodyDef;
	}

	@Override
	public Shape getGraphicShape() {
		Point position = getGraphicPosition();
		//System.out.println("Position x : " + position.x + " Position y : " + position.y);
		float radius = getRadius();
		//System.out.println("Radius x : " + Graphics.gameToGraphicX(radius) + " Radius y : " + Graphics.gameToGraphicY(radius));
		return new Ellipse2D.Float(position.x, position.y,
				Graphics.gameToGraphicX(radius),
				Graphics.gameToGraphicY(radius));
	}

	/**
	 * Paint bullet on graphics
	 * 
	 * @param graphics
	 */
	@Override
	public void draw(Graphics2D graphics) {
		if (isActive() && !isStopped()) {
			Point position = getGraphicPosition();
			//graphics.fill(getGraphicShape());
			System.out.println("Position x : " + position.x + " Position y : " + position.y);
			graphics.fillRect(position.x , position.y, 20,
				20);
		}
	}

	protected static FixtureDef getFixtureDef() {
		final FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setRadius(0.1f);
		dynamicBox.setAsBox(0.1f, 0.1f);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		return fixtureDef;
	}

	@Override
	public String toString() {
		return name;
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
		stopped = false;
		setActive(true);
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
	 * Called when contact with another body is ended
	 */
	public abstract void endContact(Body body);
}
