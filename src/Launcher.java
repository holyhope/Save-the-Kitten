import java.awt.Point;
import java.util.HashMap;
import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

public class Launcher {
	private final Point position;
	private final long waitTime = 500;
	private final Vec2 orientation;
	private final int nbCat;

	public Launcher(int x, int y, int nbCat, Vec2 orientation) {
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit être positif.");
		}
		position = new Point(x, y);
		this.orientation = Objects.requireNonNull(orientation);
		this.nbCat = nbCat;
	}

	public Launcher(int x, int y, int strengh) {
		this(x,y,1,new Vec2(strengh,0));
	}

	public HashMap<Cat, BodyDef> createBodies() {
		BodyDef bodyDef;
		HashMap<Cat,BodyDef> list = new HashMap<>();
		for (int i = 0; i < nbCat; i++) {
			bodyDef = new BodyDef();
			bodyDef.type = BodyType.DYNAMIC;
			bodyDef.position.set(position.x, position.y);
			//bodyDef.angle = (float) (Math.PI / 6);
			bodyDef.fixedRotation = false;
			bodyDef.linearVelocity = orientation;
			list.put( new Cat(), bodyDef );
		}
		return list;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public Point getPosition() {
		return new Point( position.x, position.y );
	}
}
