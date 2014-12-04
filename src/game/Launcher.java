package game;
import java.awt.Point;
import java.util.HashMap;
import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

public class Launcher {
	private final Point position;
	private final long waitTime = 500;
	private final Vec2 orientation;
	private final int nbCat;

	public Launcher(int x, int y, int nbCat, Vec2 orientation) {
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit etre positif.");
		}
		position = new Point(x, y);
		this.orientation = Objects.requireNonNull(orientation);
		this.nbCat = nbCat;
	}

	public Launcher(int x, int y, int strengh) {
		this(x,y,1,new Vec2(strengh,0));
	}

	public HashMap<Cat, Body> createBodies(World world) {
		HashMap<Cat,Body> map = new HashMap<>();
		for (int i = 0; i < nbCat; i++) {
			Cat cat = new Cat();
			map.put( cat, cat.addToWorld(world, position, orientation) );
		}
		return map;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public Point getPosition() {
		return new Point( position.x, position.y );
	}
}
