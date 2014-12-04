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

public class Cat implements GameElement {
	private final String name;
	private final FixtureDef fixtureDef;
	/**
	 * Let name cat with integer if not specified
	 */
	private static int nbCat = 0;

	public Cat(String name) {
		this.name = name;
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
	}

	public Cat() {
		this("Unamed cat (" + nbCat + ")");
		nbCat++;
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

	public Body addToWorld(World world, Point position, Vec2 velocity) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(position.x, position.y);
		// bodyDef.angle = (float) (Math.PI / 6);
		// bodyDef.fixedRotation = false;
		bodyDef.linearVelocity = velocity;
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		return body;
	}
}
