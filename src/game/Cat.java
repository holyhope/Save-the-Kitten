package game;

import java.awt.Point;

import javax.swing.JWindow;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public final class Cat extends Bullet {
	public static Cat create(World world, Point position, Vec2 velocity,
			float angularVelocity) {
		Body body;
		BodyDef bodyDef = getBodyDef(position, velocity, angularVelocity);
		do {
			body = world.createBody(bodyDef);
		} while (body == null);
		return new Cat(body);

	}

	public static Cat create(World world, Point position, Vec2 velocity) {
		return create(world, position, velocity, 0);
	}

	public Cat(Body body) {
		super(body, getFixtureDef());
	}

	@Override
	public void draw(JWindow window) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beginContact(Body body) {
		super.beginContact(body);
		stop();
	}

	@Override
	public void endContact(Body body) {
	}
}
