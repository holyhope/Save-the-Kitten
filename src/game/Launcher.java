package game;

import java.awt.Point;
import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Launcher {
	private final Point position;
	private final long waitTime = 500;
	private final Vec2 orientation;
	private final int nbCat;
	private boolean ended = false;

	public Launcher(Point position, int nbCat, Vec2 orientation) {
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit etre positif.");
		}
		Objects.requireNonNull(position);
		this.position = new Point(position.x, position.y);
		this.orientation = Objects.requireNonNull(orientation);
		this.nbCat = nbCat;
	}

	public Launcher(Point position, float d) {
		this(position, 1, new Vec2(d, 0));
	}

	public Point getPosition() {
		return new Point(position.x, position.y);
	}

	public boolean isFinished() {
		return ended;
	}

	public void launch(World world) {
		for (int i = 0; i < nbCat; i++) {
			Bullet.create(world, position, orientation);
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ended = true;
	}
}
