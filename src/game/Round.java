package game;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class Round {
	/**
	 * List of Goals
	 */
	private final LinkedHashSet<Goal> goals = new LinkedHashSet<>();
	/**
	 * List of Bullets
	 */
	private final LinkedHashSet<Bullet> bullets = new LinkedHashSet<>();
	/**
	 * List of launchers
	 */
	private final LinkedHashSet<Launcher> launchers = new LinkedHashSet<>();
	/**
	 * Width of the area
	 */
	private final float width;
	/**
	 * Height of the area
	 */
	private final float height;
	/**
	 * World of the round
	 */
	private final World world;
	/**
	 * Time use for calculating step of the round
	 */
	private final float timeStep = 1.0f / 60.0f;
	private final int velocityIterations = 6;
	private final int positionIterations = 2;
	/**
	 * Lock use for waiting end of the round
	 */
	private final Object endLock = new Object();
	/**
	 * True if the round already started
	 */
	private final AtomicBoolean started = new AtomicBoolean();
	/**
	 * List of all world where a round is already attached
	 */
	private static final Set<World> worldsAttached = Collections
			.synchronizedSet(new HashSet<>());

	private Round(World world, float width, float height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
					"Dimension of the board must be positive.");
		}
		this.width = width;
		this.height = height;
		this.world = Objects.requireNonNull(world);
		if (!worldsAttached.add(world)) {
			throw new IllegalStateException(
					"World is already attached to another round.");
		}
	}

	public static Round create(World world, float width, float height) {
		Round round = new Round(world, width, height);
		world.setContactListener(new Collide());
		round.createWalls();
		return round;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		worldsAttached.remove(world);
	}

	/**
	 * Create a wall in the world
	 * 
	 * @param x
	 *            position of the wall
	 * @param y
	 *            position of the wall
	 * @param width
	 *            of the wall
	 * @param height
	 *            of the wall
	 */
	private void createWall(float x, float y, float width, float height) {
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(x, y);
		Body wallBody = world.createBody(wallDef);
		PolygonShape wallBox = new PolygonShape();
		wallBox.setAsBox(width, height);
		Fixture fixture = wallBody.createFixture(wallBox, 0);
		Filter filter = new Filter();
		filter.categoryBits = 0x0001;
		filter.maskBits = 0xFFFF;
		fixture.setFilterData(filter);
	}

	/**
	 * Create 4 walls around the area of the round
	 */
	private void createWalls() {
		float wallSize = 0.9f;
		// Bottom Wall
		createWall(width / 2, -wallSize, width / 2 + wallSize, wallSize);
		// Top wall
		createWall(width / 2, height + wallSize, width / 2 + wallSize, wallSize);
		// Left wall
		createWall(-wallSize, height / 2, wallSize, height / 2);
		// Right wall
		createWall(width + wallSize, height / 2, wallSize, height / 2);
	}

	/**
	 * Get collections of all launcher
	 * 
	 * @return unmodifiable set of launchers
	 */
	public Set<Launcher> getLaunchers() {
		return Collections.unmodifiableSet(launchers);
	}

	/**
	 * Get collections of all bullet
	 * 
	 * @return unmodifiable set of bullets
	 */
	public Set<Bullet> getBullets() {
		return Collections.unmodifiableSet(bullets);
	}

	/**
	 * Get collections of all goal
	 * 
	 * @return unmodifiable set of goals
	 */
	public Set<Goal> getGoals() {
		return Collections.unmodifiableSet(goals);
	}

	/**
	 * Check if position is in area
	 * 
	 * @param position
	 *            to check
	 * @return True if position is in area
	 */
	public boolean isInArea(Vec2 position) {
		return position.x >= 0 && position.x < width && position.y >= 0
				&& position.y < height;
	}

	/**
	 * Check if the round ended by a victory
	 * 
	 * @return True if victory
	 */
	public boolean isVictory() {
		for (Goal goal : goals) {
			if (!goal.isFull()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the round ended with a defeat
	 * 
	 * @return True if defeat
	 */
	public boolean isDefeat() {
		return bullets.stream().map(bullet -> bullet.isStopped())
				.reduce(false, (a, b) -> a || b);
	}

	/**
	 * Calculation of a new step
	 */
	private void update() {
		try {
			world.step(timeStep, velocityIterations, positionIterations);
		} catch (Exception e) {
			// TODO
		}
		/*
		 * for (Bullet bullet : bullets) { System.out.println("Bullet: " +
		 * bullet + " " + bullet.getPosition() + " " + (bullet.isStopped() ?
		 * "stopped" : "running") + " " + bullet.isActive()); } for (Goal goal :
		 * goals) { System.out.println("Goal: " + goal + " " +
		 * goal.getPosition()); } System.out.println();
		 */
	}

	/**
	 * Let's the round begin !
	 */
	public void start() {
		if (started.getAndSet(true)) {
			throw new IllegalStateException("Le round a déjà démarré");
		}
		startLaunch();

		do {
			update();
		} while (!isVictory() && !isDefeat());

		stopLaunch();
		synchronized (endLock) {
			endLock.notifyAll();
		}
	}

	/**
	 * Make current thread waiting for the end of the round.
	 */
	public void waitForEnd() {
		synchronized (endLock) {
			while (!isVictory() && !isDefeat()) {
				try {
					endLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Stop all launcher
	 */
	private void stopLaunch() {
		launchers.stream().forEach(l -> l.stopLaunch());
	}

	/**
	 * Run all launcher
	 */
	private void startLaunch() {
		launchers.stream().map(l -> l.launch()).forEach(b -> bullets.addAll(b));
	}

	/**
	 * Add a goal to the round
	 * 
	 * @param goal
	 *            to add
	 */
	public void add(Goal goal) {
		if (isStarted()) {
			throw new IllegalStateException("Le round a déjà démarré");
		}
		Objects.requireNonNull(goal);
		if (!isInArea(goal.getPosition())) {
			throw new IllegalArgumentException("Goal must be in board.");
		}
		if (!goal.isInWorld(world)) {
			throw new IllegalStateException("Goal is not in the world.");
		}
		goals.add(goal);
	}

	/**
	 * Check if round already started
	 * 
	 * @return True if round started
	 */
	public boolean isStarted() {
		return started.get();
	}

	/**
	 * Add launcher to the round
	 * 
	 * @param launcher
	 *            to add
	 */
	public void add(Launcher launcher) {
		if (isStarted()) {
			throw new IllegalStateException("Le round a déjà démarré");
		}
		Objects.requireNonNull(launcher);
		if (!isInArea(launcher.getPosition())) {
			throw new IllegalArgumentException("Launcher must be in board.");
		}
		if (!launcher.isInWorld(world)) {
			throw new IllegalStateException("Launcher is not in the world.");
		}
		launchers.add(launcher);
	}

	/**
	 * Get width of the area
	 * 
	 * @return width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Get height of the area
	 * 
	 * @return height
	 */
	public float getHeight() {
		return height;
	}
}
