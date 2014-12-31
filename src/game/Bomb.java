package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Timer;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Bomb extends GameElement {
	static class MyQueryCallback implements QueryCallback {
		public ArrayDeque<Body> foundBodies = new ArrayDeque<Body>();

		@Override
		public boolean reportFixture(Fixture fixture) {
			foundBodies.addLast(fixture.getBody());
			return true;// keep going to find all fixtures in the query area
		}
	};

	/**
	 * Timer of the bomb.
	 */
	private final Timer timer = new Timer(getTimerPrecision(), e -> explode());
	/**
	 * Timer lock use by setTimer and startTimer.
	 */
	private final ReentrantReadWriteLock timerLock = new ReentrantReadWriteLock();
	/**
	 * Check if bomb's timer has started.
	 */
	private AtomicBoolean started = new AtomicBoolean(false);

	/**
	 * Check if Bomb has already exploded
	 */
	private AtomicBoolean hasExplosed = new AtomicBoolean(false);

	/**
	 * Radius of the blast explosion
	 */
	private static final float BLAST_RADIUS = 2;
	/**
	 * Power of the blast explosion
	 */
	private static final float BLAST_POWER = 100;
	/**
	 * Precision of the timer.
	 */
	private static final int PRECISION_TIMER = 200;
	/**
	 * Max value of the timer.
	 */
	private static final int MAX_TIMER = 3000;

	protected Bomb(Body body) {
		super(body);
		body.createFixture(getFixtureDef());
	}

	/**
	 * Change bomb's timer.
	 * 
	 * @param timer
	 *            - new positive timer.
	 * @throws IllegalStateException
	 *             Must be called only once.
	 */
	public void setTimer(int timer) {
		if (timer <= 0 || timer > getMaxTimer()) {
			throw new IllegalArgumentException("millisecond <= 0");
		}
		timer = Math.round(timer / getTimerPrecision()) * getTimerPrecision();
		timerLock.writeLock().lock();
		try {
			if (started.get()) {
				throw new IllegalStateException("Bomb's timer already started.");
			}

			this.timer.setInitialDelay(timer);
			this.timer.setDelay(this.timer.getInitialDelay());
		} finally {
			timerLock.writeLock().unlock();
		}
	}

	/**
	 * Start bomb's timer.
	 * 
	 * @throws IllegalStateException
	 *             Must be called only once.
	 */
	public void startTimer() {
		timerLock.readLock().lock();
		if (started.getAndSet(true)) {
			throw new IllegalStateException("Bomb's timer already started.");
		}
		timer.start();
		timerLock.readLock().unlock();
	}

	/**
	 * Get precision of the timer's bomb.
	 * 
	 * @return precision of the bomb.
	 */
	public int getTimerPrecision() {
		return PRECISION_TIMER;
	}

	/**
	 * Get max timer's value of the bomb.
	 * 
	 * @return max value.
	 */
	public int getMaxTimer() {
		return MAX_TIMER;
	}

	/**
	 * Apply an impulse to body at applyPoint from blastCenter with blastPower
	 * strength.
	 * 
	 * @param body
	 *            to impulse.
	 * @param blastCenter
	 *            - where the impulse come from
	 * @param applyPoint
	 *            - where the impulse apply.
	 * @param blastPower
	 *            - strength of the impulse
	 */
	protected void applyBlastImpulse(Body body, Vec2 blastCenter,
			Vec2 applyPoint, float blastPower) {
		// ignore any non-dynamic bodies
		if (body.getType() != BodyType.DYNAMIC)
			return;
		Vec2 blastDir = new Vec2(applyPoint.add(blastCenter.negate()));
		float distance = blastDir.normalize();
		// ignore bodies exactly at the blast point - blast direction is
		// undefined
		if (distance == 0)
			return;
		float invDistance = 1 / distance;
		float impulseMag = blastPower * invDistance * invDistance;
		impulseMag = Math.min(impulseMag, 500.0f);
		body.applyLinearImpulse(blastDir.mul(impulseMag), applyPoint);
	}

	/**
	 * Make the bomb booming.
	 * 
	 * @throws IllegalStateException
	 *             Must be called only once.
	 */
	protected void explode() {
		timer.stop();
		if (hasExplosed.getAndSet(true)) {
			throw new IllegalStateException("Bomb has already exploded");
		}
		Vec2 center = getPosition();

		// find all fixtures within blast radius AABB
		MyQueryCallback queryCallback = new MyQueryCallback();
		Vec2 vec = new Vec2(Math.round(BLAST_RADIUS), Math.round(BLAST_RADIUS));
		AABB aabb = new AABB(center.add(vec.negate()), center.add(vec));
		getWorld().queryAABB(queryCallback, aabb);

		// check which of these have their center of mass within the blast
		// radius
		for (int i = 0; i < queryCallback.foundBodies.size(); i++) {
			Body body = queryCallback.foundBodies.pollFirst();
			Vec2 bodyCom = body.getWorldCenter();
			// ignore bodies outside the blast range
			if (new Vec2(bodyCom.add(center.negate())).length() >= BLAST_RADIUS)
				continue;
			applyBlastImpulse(body, center, bodyCom, BLAST_POWER * 0.05f);
		}
	}

	public static Bomb create(World world, Vec2 position) {
		BodyDef bodyDef = new BodyDef();
		Objects.requireNonNull(position);
		bodyDef.position.set(position.x, position.y);
		bodyDef.type = BodyType.STATIC;
		bodyDef.awake = true;
		bodyDef.active = false;
		Body body = Objects.requireNonNull(world).createBody(bodyDef);
		return new Bomb(body);
	}

	private static FixtureDef getFixtureDef() {
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setRadius(.2f);
		dynamicBox.setAsBox(.4f, .4f);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		return fixtureDef;
	}

	@Override
	public void draw(Graphics2D graphics) {
		if (hasExplosed.get()) {
			return;
		}
		Color color = graphics.getColor();
		Font font = graphics.getFont();
		float delay = this.timer.getDelay();
		int InitialTimer = timer.getInitialDelay();
		graphics.setColor(new Color((InitialTimer - Math.round(delay)) * 255
				/ InitialTimer, Math.round(delay) * 255 / InitialTimer, 100));
		super.draw(graphics);
		Point position = getGraphicPosition();
		FontMetrics fontMetrics = graphics.getFontMetrics();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(delay / 1000);
		graphics.setColor(Color.YELLOW);
		graphics.setFont(new Font(font.getName(), Font.BOLD, 12));
		String string = stringBuilder.toString();
		graphics.drawString(string,
				position.x - fontMetrics.stringWidth(string) / 2, position.y
						- fontMetrics.getHeight() / 2);
		graphics.setFont(font);
		graphics.setColor(color);
	}

	/**
	 * 
	 * @param classValue
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 */
	public static Method getConstructor(Class<? extends Bomb> classValue)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException {
		Method method = classValue.getDeclaredMethod("create", World.class,
				Vec2.class);
		if (Bullet.class.isInstance(method.getReturnType())) {
			throw new IllegalAccessException();
		}
		return method;
	}
}
