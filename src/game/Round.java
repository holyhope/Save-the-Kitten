package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	 * List of Goals.
	 */
	private final LinkedHashSet<Goal> goals = new LinkedHashSet<>();
	/**
	 * List of Bullets.
	 */
	private final LinkedHashSet<Bullet> bullets = new LinkedHashSet<>();
	/**
	 * List of launchers.
	 */
	private final LinkedHashSet<Launcher> launchers = new LinkedHashSet<>();
	/**
	 * List of bomb planted on board which has not exploded.
	 */
	private final LinkedHashSet<Bomb> bombs = new LinkedHashSet<>();
	/**
	 * Number of bomb to plant.
	 */
	private final ConcurrentLinkedQueue<Class<? extends Bomb>> bombToPlant = new ConcurrentLinkedQueue<>();
	/**
	 * Width of the area.
	 */
	private final float width;
	/**
	 * Height of the area.
	 */
	private final float height;
	/**
	 * World of the round.
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
	/**
	 * Color of the wall in Graphics2D.
	 */
	private static final Color WALL_COLOR = new Color(50, 50, 50);

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

	public World getWorld() {
		return world;
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
	public void createWall(float x, float y, float width, float height) {
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(x + width / 2, y + height / 2);
		Body wallBody = world.createBody(wallDef);
		PolygonShape wallBox = new PolygonShape();
		wallBox.setAsBox(width / 2, height / 2);
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
		float wallSize = 1f;
		// Bottom Wall
		createWall(-wallSize, -wallSize, width + wallSize * 2, wallSize);
		// Top wall
		createWall(-wallSize, height + wallSize, width + wallSize * 2, wallSize);
		// Left wall
		createWall(-wallSize, 0, wallSize, height);
		// Right wall
		createWall(width, 0, wallSize, height);
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
	 * Get collections of all bombs
	 * 
	 * @return unmodifiable set of bombs
	 */
	public Set<Bomb> getBombs() {
		return Collections.unmodifiableSet(bombs);
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
		return bullets.stream().allMatch(Bullet::isStarted)
				&& bullets.stream().anyMatch(Bullet::isStopped) && !isVictory();
	}

	/**
	 * Calculation of a new step
	 */
	private void update() {
		try {
			world.step(timeStep, velocityIterations, positionIterations);
		} catch (Exception e) {
		}
	}

	/**
	 * Let's the round begin !
	 */
	public void start() {
		if (started.getAndSet(true)) {
			throw new IllegalStateException("Round already started.");
		}
		startLaunch();
		startBombs();

		do {
			update();
		} while (!isVictory() && !isDefeat());

		stopLaunch();

		synchronized (endLock) {
			endLock.notifyAll();
		}
	}

	/**
	 * Start timer of all bombs.
	 */
	private void startBombs() {
		bombs.stream().forEach(Bomb::startTimer);
	}

	/**
	 * Make current thread waiting for the end of the round.
	 * 
	 * @throws InterruptedException
	 */
	public void waitForEnd() throws InterruptedException {
		synchronized (endLock) {
			while (!isVictory() && !isDefeat()) {
				endLock.wait();
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
			throw new IllegalStateException("Round already started.");
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
			throw new IllegalStateException("Round already started.");
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
	 * Add bomb that user can plant.
	 * 
	 * @param classValue
	 *            of the bomb.
	 * @param position
	 *            of the bomb
	 * @return True in case of success.
	 */
	public boolean add(Class<? extends Bomb> classValue) {
		return bombToPlant.add(classValue);
	}

	/**
	 * Plant next bomb to position.
	 * 
	 * @param position
	 *            of the next bomb.
	 * @return True in case of success.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Bomb plantNextBomb(Vec2 position) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		if (isStarted()) {
			throw new IllegalStateException("Round already started.");
		}
		if (!isInArea(Objects.requireNonNull(position))) {
			throw new IllegalArgumentException("Bomb must be in board.");
		}
		Bomb bomb = (Bomb) Bomb.getConstructor(bombToPlant.poll()).invoke(null,
				world, position);
		if (!bomb.isInWorld(world)) {
			throw new IllegalStateException("Bomb is not in the world.");
		}
		if (!bombs.add(bomb)) {
			throw new IllegalStateException("Can't plant bomb.");
		}
		return bomb;
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

	/**
	 * Draw walls in area.
	 * 
	 * @param graphics
	 *            to draw in
	 */
	public void draw(Graphics2D graphics2D) {
		Vec2 vertices[] = new Vec2[4];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vec2();
		}
		graphics2D.setColor(WALL_COLOR);
		Body body = world.getBodyList();
		while (body != null) {
			if (body.getUserData() == null) {
				try {
					body.getFixtureList().getAABB(0).getVertices(vertices);

					int x = Arrays.asList(vertices).stream()
							.map(v -> Graphics.gameToGraphicX(v.x)).distinct()
							.reduce(Integer.MAX_VALUE, Math::min);
					int y = Arrays.asList(vertices).stream()
							.map(v -> Graphics.gameToGraphicY(v.y)).distinct()
							.reduce(Integer.MAX_VALUE, Math::min);
					int width = Math.abs(Arrays.asList(vertices).stream()
							.map(v -> Graphics.gameToGraphicX(v.x)).distinct()
							.reduce(Integer.MIN_VALUE, Math::max)
							- x);
					int height = Math.abs(Arrays.asList(vertices).stream()
							.map(v -> Graphics.gameToGraphicY(v.y)).distinct()
							.reduce(Integer.MIN_VALUE, Math::max)
							- y);

					graphics2D.fillRect(x, y, width, height);
				} catch (Throwable t) {
					Graphics.addException(t);
				}
			}
			body = body.getNext();
		}
	}

	/**
	 * Check if there is bomb to plant.
	 * 
	 * @return True if there is still a bomb.
	 */
	public boolean canPlantBomb() {
		return !bombToPlant.isEmpty();
	}
}
