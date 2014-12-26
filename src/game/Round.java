package game;

import java.awt.Dimension;
import java.util.LinkedHashSet;
import java.util.Objects;

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
	private final Dimension dimension;
	private final Launcher launcher;
	private final float timeStep = 1.0f / 60.0f;
	private final int velocityIterations = 6;
	private final int positionIterations = 2;

	private Round(Dimension dimension, Launcher launcher) {
		Objects.requireNonNull(dimension);
		if (dimension.width <= 0 || dimension.height <= 0) {
			throw new IllegalArgumentException(
					"Dimension of the board must be positive.");
		}
		this.dimension = new Dimension(dimension.width, dimension.height);
		Objects.requireNonNull(launcher);
		if (!isInBoard(launcher.getBody().getPosition())) {
			throw new IllegalArgumentException("Launcher must be in board.");
		}
		this.launcher = launcher;
	}

	private World getWorld() {
		return launcher.getBody().getWorld();
	}

	public static Round create(Dimension dimension, Launcher launcher) {
		Round round = new Round(dimension, launcher);
		round.getWorld().setContactListener(new Collide());
		round.createWalls();
		return round;
	}

	private void createWall(int x, int y, int width, int height) {
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(x, y);
		Body wallBody = getWorld().createBody(wallDef);
		PolygonShape wallBox = new PolygonShape();
		wallBox.setAsBox(width, height);
		Fixture fixture = wallBody.createFixture(wallBox, 0);
		Filter filter = new Filter();
		filter.categoryBits = 0x0001;
		filter.maskBits = 0xFFFF;
		fixture.setFilterData(filter);
	}

	private void createWalls() {
		final int wallSize = 1;

		// Bottom Wall
		createWall(dimension.width / 2, -wallSize, dimension.width / 2
				+ wallSize, wallSize);
		// Top wall
		createWall(dimension.width / 2, dimension.height + wallSize,
				dimension.width / 2 + wallSize, wallSize);
		// Left wall
		createWall(-wallSize, dimension.height / 2, wallSize,
				dimension.height / 2);
		// Right wall
		createWall(dimension.width + wallSize, dimension.height / 2, wallSize,
				dimension.height / 2);
	}

	public boolean isInBoard(Vec2 position) {
		return position.x >= 0 && position.x < dimension.width
				&& position.y >= 0 && position.y < dimension.height;
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
			getWorld().step(timeStep, velocityIterations, positionIterations);
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		for (Bullet bullet : bullets) {
			System.out.println("Bullet: " + bullet + " "
					+ bullet.getBody().getPosition() + " "
					+ (bullet.isStopped() ? "stopped" : "running") + " "
					+ bullet.getBody().isActive());
		}
		for (Goal goal : goals) {
			System.out.println("Goal: " + goal + " "
					+ goal.getBody().getPosition());
		}
		System.out.println();
	}

	public void start() {
		startLaunch();

		do {
			update();
		} while (!isVictory() && !isDefeat());

		launcher.stopLaunch();
	}

	private void startLaunch() {
		bullets.addAll(launcher.launch());
	}

	public void add(Goal goal) {
		goals.add(Objects.requireNonNull(goal));
	}
}
