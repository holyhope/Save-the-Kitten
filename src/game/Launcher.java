package game;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Launcher extends GameElement {
	/**
	 * Number of millisecond between two bullet
	 */
	private final long waitTime = 250;
	/**
	 * Orientation of the canon
	 */
	private final Vec2 orientation;
	/**
	 * List of bullets which will be launched
	 */
	private final ConcurrentLinkedQueue<Bullet> bullets = new ConcurrentLinkedQueue<>();
	/**
	 * True if launcher stopped
	 */
	private boolean stop = false;
	/**
	 * Used for generating random angle for launched bullets.
	 */
	private final Random randomAngle = new Random();

	private Launcher(Body body, Vec2 position, int nbCat, Vec2 orientation) {
		super(body);
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit etre positif.");
		}
		Objects.requireNonNull(position);
		Objects.requireNonNull(orientation);
		this.orientation = new Vec2(orientation.x, orientation.y);
	}

	/**
	 * Create, in world, at position, an horizontal launcher which throws 1 cat.
	 * 
	 * @param world
	 *            of the launcher
	 * @param position
	 *            of the launcher
	 * @param strengh
	 *            of the canon
	 * @return new Launcher
	 */
	public static Launcher create(World world, Vec2 position, float strengh) {
		return create(world, position, 1, new Vec2(strengh, 0));
	}

	/**
	 * Create, in world, at position, with an orientation, a launcher which
	 * throws number cat.
	 * 
	 * @param world
	 *            of the launcher
	 * @param position
	 *            of the launcher
	 * @param number
	 *            of bullet to launch
	 * @param orientation
	 *            of the canon
	 * @return new Launcher
	 */
	public static Launcher create(World world, Vec2 position, int number,
			Vec2 orientation) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.active = true;
		Objects.requireNonNull(position);
		Vec2 orientationNormalized = new Vec2();
		orientationNormalized.set(orientation).normalize();
		bodyDef.angle = (float) Math.acos(Vec2.dot(new Vec2(1, 0),
				orientationNormalized));
		bodyDef.position.set(position.x, position.y);
		Body body = Objects.requireNonNull(world).createBody(bodyDef);
		Launcher launcher = new Launcher(body, position, number, orientation);
		return launcher;
	}

	/**
	 * Stop launcher's launch
	 */
	public void stopLaunch() {
		stop = true;
	}

	/**
	 * Add a bullet to the canon.
	 * 
	 * @param Class
	 *            of the new bullet
	 * @return True in case of success.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 *             if callValue does not have static create method
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public boolean addBullet(Class<? extends Bullet> classValue)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return bullets.add((Bullet) Bullet.getConstructor(classValue).invoke(
				null, getWorld(), getPosition(), orientation,
				randomAngle.nextFloat()));
	}

	/**
	 * Make the launcher launch its bullets
	 * 
	 * @return Set of bullets which will be launched
	 */
	public Set<Bullet> launch() {
		final LinkedHashSet<Bullet> set = new LinkedHashSet<>();
		set.addAll(bullets);
		bullets.removeAll(set);

		new Thread(() -> {
			for (Bullet bullet : set) {
				if (stop) {
					return;
				}
				bullet.start();
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		return set;
	}

	/**
	 * Get the radius of the element.
	 * 
	 * @return radius of the element.
	 */
	public float getRadius() {
		return .2f;
	}

	@Override
	public Shape getGraphicShape() {
		Point position = getGraphicPosition();
		int height = Math.abs(Graphics.gameToGraphicY(.15f));
		int width = Math.abs(Graphics.gameToGraphicX(.35f));

		Area area = new Area(transformShape(new Rectangle(position.x,
				position.y - height / 2, width, height)));

		int radiusX = Math.abs(Graphics.gameToGraphicX(getRadius()));
		int radiusY = Math.abs(Graphics.gameToGraphicY(getRadius()));
		area.add(new Area(new Ellipse2D.Float(position.x - radiusX, position.y
				- radiusY, radiusX * 2, radiusY * 2)));

		radiusX *= 3f / 4;
		radiusY -= 1;
		area.add(new Area(new Rectangle(position.x - radiusX, position.y + 1,
				radiusX * 2, radiusY)));
		return area;
	}

	public void addBullet(Function<?, Bullet> bullet) {

	}
}
