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
	private final LinkedHashSet<Goal> goals = new LinkedHashSet<>();
	private final LinkedHashSet<Bullet> bullets = new LinkedHashSet<>();
	private final LinkedHashSet<Launcher> launchers = new LinkedHashSet<>();
	private final float width;
	private final float height;
	private final World world;
	private final float timeStep = 1.0f / 60.0f;
	private final int velocityIterations = 6;
	private final int positionIterations = 2;
	private Object endLock = new Object();
	private final AtomicBoolean started = new AtomicBoolean();
	private static final Set<World> worldsAttached = Collections
			.synchronizedSet(new HashSet<>());
	private final float wallSize = 0.9f;

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

	private void createWalls() {
		// Bottom Wall
		createWall(width / 2, -wallSize, width / 2 + wallSize, wallSize);
		// Top wall
		createWall(width / 2, height + wallSize, width / 2 + wallSize, wallSize);
		// Left wall
		createWall(-wallSize, height / 2, wallSize, height / 2);
		// Right wall
		createWall(width + wallSize, height / 2, wallSize, height / 2);
	}

	public Set<Launcher> getLaunchers() {
		return launchers;
	}

	public Set<Bullet> getBullets() {
		return bullets;
	}

	public Set<Goal> getGoals() {
		return goals;
	}

	public boolean isInBoard(Vec2 position) {
		return position.x >= 0 && position.x < width && position.y >= 0
				&& position.y < height;
	}

	public boolean isVictory() {
		for (Goal goal : goals) {
			if (!goal.isFull()) {
				return false;
			}
		}
		return true;
	}

	public boolean isDefeat() {
		return bullets.stream().map(bullet -> bullet.isStopped())
				.reduce(false, (a, b) -> a || b);
	}

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
		endLock = new Object();
	}

	private void stopLaunch() {
		launchers.stream().forEach(l -> l.stopLaunch());
	}

	private void startLaunch() {
		launchers.stream().map(l -> l.launch()).forEach(b -> bullets.addAll(b));
	}

	public void add(Goal goal) {
		if (isStarted()) {
			throw new IllegalStateException("Le round a déjà démarré");
		}
		Objects.requireNonNull(goal);
		if (!isInBoard(goal.getPosition())) {
			throw new IllegalArgumentException("Goal must be in board.");
		}
		if (!goal.isInWorld(world)) {
			throw new IllegalStateException("Goal is not in the world.");
		}
		goals.add(goal);
	}

	public boolean isStarted() {
		return started.get();
	}

	public void add(Launcher launcher) {
		if (isStarted()) {
			throw new IllegalStateException("Le round a déjà démarré");
		}
		Objects.requireNonNull(launcher);
		if (!isInBoard(launcher.getPosition())) {
			throw new IllegalArgumentException("Launcher must be in board.");
		}
		if (!launcher.isInWorld(world)) {
			throw new IllegalStateException("Launcher is not in the world.");
		}
		launchers.add(launcher);
	}

	public Object getEndLock() {
		return endLock;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
