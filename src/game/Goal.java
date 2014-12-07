package game;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Objects;

import javax.swing.JWindow;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Goal extends GameElement {
	private Goal(Body body) {
		super(body);
	}

	private final int slot = 1;
	private final LinkedHashSet<Bullet> bullets = new LinkedHashSet<>();

	public static Goal create(World world, Point position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position.x, position.y);
		bodyDef.type = BodyType.STATIC;
		Body body = world.createBody(bodyDef);
		Goal goal = new Goal(body);
		body.setUserData(goal);
		return goal;
	}

	@Override
	public void draw(JWindow window) {
		// TODO Auto-generated method stub

	}

	public boolean receive(Bullet bullet) {
		Objects.requireNonNull(bullet);
		if ( ! bullet.getBody().getWorld().equals(getBody().getWorld()) ) {
			throw new IllegalArgumentException("Bullet and Goal are not in the same World.");
		}
		return bullets.add(bullet);
	}

	public boolean isFull() {
		return slot == bullets.size();
	}
}
