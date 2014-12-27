package game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Launcher extends GameElement {
	/**
	 * Number of millisecond between two bullet
	 */
	private final long waitTime = 1500;
	/**
	 * Orientation of the canon
	 */
	private final Vec2 orientation;
	/**
	 * Total amount of cat to launch
	 */
	private final int nbCat;
	/**
	 * True if launcher stopped
	 */
	private boolean stop = false;

	private Launcher(Body body, Vec2 position, int nbCat, Vec2 orientation) {
		super(body);
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit etre positif.");
		}
		Objects.requireNonNull(position);
		Objects.requireNonNull(orientation);
		this.orientation = new Vec2(orientation.x, orientation.y);
		this.nbCat = nbCat;
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
	 * Make the launcher launch its bullets
	 * 
	 * @return Set of bullets which will be launched
	 */
	public Set<Bullet> launch() {
		final LinkedHashSet<Bullet> set = new LinkedHashSet<>();
		Random random = new Random();
		for (int i = 0; i < nbCat; i++) {
			Vec2 position = getPosition();
			set.add(Cat.create(getWorld(), new Vec2(Math.round(position.x),
					Math.round(position.y)), orientation, random.nextFloat()));
		}

		final LinkedHashSet<Bullet> setFinal = new LinkedHashSet<>();
		setFinal.addAll(set);
		new Thread(() -> {
			for (Bullet bullet : setFinal) {
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
	 * Draw launcher in graphics
	 */
	@Override
	public void draw(Graphics2D graphics) {
		Vec2 vec2 = getPosition();
		Point position = new Point(Math.round(vec2.x * Graphics.PRECISION),
				Math.round(vec2.y * Graphics.PRECISION));
		int sizeCanon = Math.round(0.1f * Graphics.PRECISION);
		int lenghtCanon = Math.round(0.3f * Graphics.PRECISION);
		int size = Math.round(0.3f * Graphics.PRECISION);

		Rectangle rectangle = new Rectangle(position.x - sizeCanon / 2,
				position.y - sizeCanon / 2, lenghtCanon, sizeCanon);
		AffineTransform affineTransform = AffineTransform.getRotateInstance(
				Math.acos(Vec2.dot(orientation,
						new Vec2(orientation.normalize(), 0))), position.x,
				position.y);
		Polygon polygon = new Polygon();
		PathIterator pathIterator = rectangle.getPathIterator(affineTransform);
		while (!pathIterator.isDone()) {
			double[] xy = new double[2];
			pathIterator.currentSegment(xy);
			polygon.addPoint((int) xy[0], (int) xy[1]);

			pathIterator.next();
		}

		graphics.fillPolygon(polygon);
		graphics.fillOval(position.x - size / 2, position.y - size / 2, size,
				size);
		graphics.fillRect(position.x - size / 2 + 1, position.y, size - 2,
				size / 2);
	}
}
