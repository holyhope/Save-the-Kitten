import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Round {
	private final HashMap<GameElement, Body> elements = new HashMap<>();
	private final LinkedList<Goal> goals = new LinkedList<>();
	private final LinkedList<Cat> cats = new LinkedList<>();
	private final Dimension dimension;
	private final World world = new World(new Vec2(0, 0));
	private final Launcher launcher;

	private Round(int width, int height, Launcher launcher) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
					"Board dimension must be positive.");
		}
		dimension = new Dimension(width, height);

		Objects.requireNonNull(launcher);
		if (!isInBoard(launcher.getPosition())) {
			throw new IllegalArgumentException("Launcher must be in board.");
		}
		this.launcher = launcher;
	}

	public static Round construct(int width, int height, Launcher launcher) {
		Round round = new Round(width, height, launcher);
		round.createWalls();
		return round;
	}

	private void createWall(int x, int y, int width, int height) {
		// TODO Check why wall does not work in world.step()
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(x, y);
		Body wallBody = world.createBody(wallDef);
		PolygonShape wallBox = new PolygonShape();
		wallBox.setAsBox(width, height);
		wallBody.createFixture(wallBox, 0);
	}

	private void createWalls() {
		final int wallSize = 10;

		createWall(dimension.width / 2, -wallSize, dimension.width / 2
				+ wallSize, wallSize);
		createWall(dimension.width / 2, dimension.height + wallSize,
				dimension.width / 2 + wallSize, wallSize);
		createWall(-wallSize, dimension.height / 2, wallSize,
				dimension.height / 2);
		createWall(dimension.width + wallSize, dimension.height / 2, wallSize,
				dimension.height / 2);
	}

	public boolean isInBoard(Point position) {
		return position.x >= 0 && position.x < dimension.width
				&& position.y >= 0 && position.y < dimension.height;
	}

	private void addElement(GameElement element, Point position) {
		Objects.requireNonNull(element);
		if (!isInBoard(position)) {
			throw new IllegalArgumentException("Coordinates of " + element
					+ " is not valid.");
		}
		BodyDef body = new BodyDef();
		body.position.set(position.x, position.y);
		body.type = BodyType.STATIC;
		elements.put(element, world.createBody(body));
	}

	public void add(GameElement element, Point position) {
		addElement(element, position);
	}

	public void add(Goal element, Point position) {
		addElement(element, position);
		goals.add(element);
	}

	private boolean isVictory() {
		for (Goal goal : goals) {
			for (Cat cat : cats) {
				if (!elements.get(goal).getPosition()
						.equals(elements.get(cat).getPosition())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isDefeat() {
		Vec2 vecNull = new Vec2(0, 0);
		for (Cat cat : cats) {
			if (cat.isLaunched()
					&& elements.get(cat).getLinearVelocity().equals(vecNull)) {
				return true;
			}
		}
		return false;
	}

	private boolean isFinnished() {
		return isVictory() || isDefeat();
	}

	private void update() {
		float timeStep = 1.0f / 60.0f;
		int velocityIterations = 6;
		int positionIterations = 2;

		world.step(timeStep, velocityIterations, positionIterations);
		for (Cat cat : cats) {
			Body body = elements.get(cat);
			Vec2 position = body.getPosition();
			float angle = body.getAngle();
			System.out.printf(cat + " %4.2f %4.2f %4.2f\n", position.x,
					position.y, angle);
		}
	}

	public void start() {
		startLaunch();

		while (!isFinnished()) {
			update();
		}
	}

	private void startLaunch() {
		Runnable r = () -> {
			Cat cat;
			final HashMap<Cat, BodyDef> bodies = launcher.createBodies();
			final long waitTime = launcher.getWaitTime();
			for (Entry<Cat, BodyDef> entry : bodies.entrySet()) {
				cat = entry.getKey();
				cats.add(cat);
				elements.put(cat, world.createBody(entry.getValue()));
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		r.run();
	}

}
