package game;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public final class Cat extends Bullet {
	/**
	 * Create a cat
	 * 
	 * @param world
	 *            of the cat
	 * @param position
	 *            of the cat
	 * @param velocity
	 *            of the cat
	 * @param angularVelocity
	 *            of the cat. Make it roll !
	 * @return new Cat
	 */
	public static Cat create(World world, Vec2 position, Vec2 velocity,
			Float angularVelocity) {
		Body body;
		BodyDef bodyDef = getBodyDef(position, velocity, angularVelocity);
		do {
			body = world.createBody(bodyDef);
		} while (body == null);
		return new Cat(body);

	}

	@Override
	public Shape getGraphicShape() {
		Point position = getGraphicPosition();
		int radiusX = Math.abs(Graphics.gameToGraphicX(getRadius()));
		int radiusY = Math.abs(Graphics.gameToGraphicY(getRadius()));
		Shape shape = super.getGraphicShape();
		Area area = new Area(shape);

		// Add ears
		Polygon ear = new Polygon();
		ear.addPoint(position.x, position.y - radiusY);
		ear.addPoint(position.x - radiusX / 3, position.y - (4 * radiusY) / 3);
		ear.addPoint(position.x - radiusX / 2, position.y - radiusY);
		area.add(new Area(transformShape(ear)));
		ear = new Polygon();
		ear.addPoint(position.x, position.y - radiusY);
		ear.addPoint(position.x + radiusX / 3, position.y - (4 * radiusY) / 3);
		ear.addPoint(position.x + radiusX / 2, position.y - radiusY);
		area.add(new Area(transformShape(ear)));

		return shape;
	}

	private Cat(Body body) {
		super(body, getFixtureDef());
	}

	/**
	 * Stop the cat.
	 * 
	 * @param body
	 *            of the other element.
	 */
	@Override
	public void beginContact(Body body) {
		super.beginContact(body);
		stop();
	}

	@Override
	public void endContact(Body body) {
	}
}
