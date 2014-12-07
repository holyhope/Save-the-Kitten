package game;

import java.awt.Point;

import javax.swing.JWindow;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Bullet extends GameElement {
	private final String name;
	private final FixtureDef fixtureDef;
	private boolean catched = false;
	/**
	 * Let name cat with integer if not specified
	 */
	private static int nbCat = 0;

	public static Bullet create(World world, Point position, Vec2 velocity) {
		return create("Unamed cat (" + nbCat + ")", world, position, velocity);
	}

	public static Bullet create(String name, World world, Point position,
			Vec2 velocity) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(position.x, position.y);
		bodyDef.linearVelocity = velocity;
		Body body = world.createBody(bodyDef);
		while (body == null) {
			synchronized(world) {
				body = world.createBody(bodyDef);
			}
		}
		Bullet bullet = new Bullet(name, body);
		body.setUserData(bullet);
		return bullet;
	}

	private Bullet(String name, Body body) {
		super(body);
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		body.createFixture(fixtureDef);
		this.name = name;
	}

	@Override
	public void draw(JWindow window) {
		// TODO Auto-generated method stub
	}

	public boolean isLaunched() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isCatched() {
		return catched;
	}

	public boolean score(Goal goal) {
		if (catched) {
			return false;
		}
		catched = goal.receive(this);
		return catched;
	}
}
