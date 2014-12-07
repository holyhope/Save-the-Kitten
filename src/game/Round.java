package game;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class Round {
	private final HashMap<Goal, Body> goals = new HashMap<>();
	private final HashMap<Bullet, Body> bullets = new HashMap<>();
	private final Dimension dimension;
	private final World world;
	private final Launcher launcher;

	private Round(Dimension dimension, World world, Launcher launcher) {
		this.world = Objects.requireNonNull(world);
		Objects.requireNonNull(dimension);
		if (dimension.width <= 0 || dimension.height <= 0) {
			throw new IllegalArgumentException(
					"Dimension of the board must be positive.");
		}
		this.dimension = new Dimension(dimension.width, dimension.height);
		Objects.requireNonNull(launcher);
		if (!isInBoard(launcher.getPosition())) {
			throw new IllegalArgumentException("Launcher must be in board.");
		}
		this.launcher = launcher;
	}

	public static Round create(Dimension dimension, World world,
			Launcher launcher) {
		Round round = new Round(dimension, world, launcher);
		round.createWalls();
		return round;
	}

	private void createWall(int x, int y, int width, int height) {
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(x, y);
		Body wallBody = world.createBody(wallDef);
		PolygonShape wallBox = new PolygonShape();
		wallBox.setAsBox(width, height);
		wallBody.createFixture(wallBox, 0);
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

	public boolean isInBoard(Point position) {
		return position.x >= 0 && position.x < dimension.width
				&& position.y >= 0 && position.y < dimension.height;
	}

	private boolean isVictory() {
		for (Entry<Goal, Body> entryGoal : goals.entrySet()) {
			if (!entryGoal.getKey().isFull()) {
				return false;
			}
		}
		return true;
	}

	private boolean isDefeat() {
		Vec2 vecNull = new Vec2(0, 0);
		if (!launcher.isFinished()) {
			return false;
		}
		for (Entry<Bullet, Body> entryBullet : bullets.entrySet()) {
			if (entryBullet.getKey().isLaunched()
					&& !entryBullet.getKey().isCatched()
					&& entryBullet.getValue().getLinearVelocity().equals(vecNull)) {
				return true;
			}
		}
		return false;
	}

	private boolean isFinnished() {
		return isVictory() || isDefeat();
	}

	private void updateLists() {
		Body body = world.getBodyList();
		Object userData;
		while (body != null) {
			userData = body.getUserData();
			if (userData instanceof Bullet) {
				Bullet bullet = (Bullet) userData;
				bullets.put(bullet, bullet.getBody());
			} else if (userData instanceof Goal) {
				Goal goal = (Goal) userData;
				goals.put(goal, goal.getBody());
			}
			body = body.getNext();
		}
	}

	private void update() {
		float timeStep = 1.0f / 60.0f;
		int velocityIterations = 6;
		int positionIterations = 2;

		System.out.println("Update !");
		synchronized (world) {
			world.step(timeStep, velocityIterations, positionIterations);
		}
		updateLists();
		for (Entry<Bullet, Body> entryBullet : bullets.entrySet()) {
			System.out.println("Bullet: " + entryBullet.getKey().toString() + " "
					+ entryBullet.getValue().getPosition());
		}
		for (Entry<Goal, Body> entryGoal : goals.entrySet()) {
			System.out.println("Goal: " + entryGoal.getKey().toString() + " "
					+ entryGoal.getValue().getPosition());
		}
		System.out.println("Fin");
	}

	public void start() {
		startLaunch();

		do {
			update();
		} while (!isFinnished());
	}

	private void startLaunch() {
		new Thread(() -> {
			launcher.launch(world);
		}).start();
	}

}
