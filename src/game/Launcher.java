package game;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Launcher {
	private final Point position;
	private final long waitTime = 1500;
	private final Vec2 orientation;
	private final int nbCat;

	public Launcher(Point position, int nbCat, Vec2 orientation) {
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit etre positif.");
		}
		Objects.requireNonNull(position);
		this.position = new Point(position.x, position.y);
		Objects.requireNonNull(orientation);
		this.orientation = new Vec2(orientation.x, orientation.y);
		this.nbCat = nbCat;
	}

	public Launcher(Point position, float d) {
		this(position, 1, new Vec2(d, 0));
	}

	public Point getPosition() {
		return new Point(position.x, position.y);
	}

	public Vec2 getOrientation() {
		return new Vec2(orientation.x, orientation.y);
	}

	public Set<Bullet> launch(World world) {
		final LinkedHashSet<Bullet> set = new LinkedHashSet<>();
		Random random = new Random();
		for (int i = 0; i < nbCat; i++) {
			set.add(Cat.create(world, getPosition(), getOrientation(), random.nextFloat()));
		}

		final LinkedHashSet<Bullet> setFinal = new LinkedHashSet<>();
		setFinal.addAll(set);
		new Thread(() -> {
			for (Bullet bullet : setFinal) {
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
}
