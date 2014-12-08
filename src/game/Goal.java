package game;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Objects;

import javax.swing.JWindow;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Goal extends GameElement {
	private final int nbSlot = 1;
	private final LinkedHashSet<Bullet> bullets = new LinkedHashSet<>();

	private Goal(Body body) {
		super(body);
		body.createFixture(getFixtureDef());
	}

	public static Goal create(World world, Point position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position.x, position.y);
		bodyDef.type = BodyType.STATIC;
		bodyDef.awake = true;
		Body body = world.createBody(bodyDef);
		return new Goal(body);
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
		return nbSlot == bullets.size();
	}

	private static FixtureDef getFixtureDef() {
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		return fixtureDef;
	}
}
