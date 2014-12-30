package game;

import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Timer;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Bomb extends GameElement {
	static class MyQueryCallback implements QueryCallback {
		public ArrayDeque<Body> foundBodies;

		@Override
		public boolean reportFixture(Fixture fixture) {
			foundBodies.addLast(fixture.getBody());
			return true;// keep going to find all fixtures in the query area
		}
	};

	/**
	 * Timer of the bomb
	 */
	private Timer timer = null;

	/**
	 * Check if Bomb has already exploded
	 */
	private static AtomicBoolean hasExplosed = new AtomicBoolean();

	/**
	 * Radius of the blast explosion
	 */
	private static final float BLAST_RADIUS = 10;
	/**
	 * Power of the blast explosion
	 */
	private static final float BLAST_POWER = 1000;

	private Bomb(Body body) {
		super(body);
		body.createFixture(getFixtureDef());
	}

	public void setTimer(int millisecond) {
		if (millisecond <= 0) {
			throw new IllegalArgumentException("millisecond <= 0");
		}
		// Création d'une instance de listener
		// associée au timer
		ActionListener action = new ActionListener() {
			// Méthode appelée à chaque tic du timer
			public void actionPerformed(ActionEvent event) {
				explode_proximity();
			}
		};

		timer = new Timer(millisecond, action);
	}

	public void startTimer() {
		timer.start();
	}

	public void stopTimer() {
		timer.stop();
	}

	private static void applyBlastImpulse(Body body, Vec2 blastCenter,
			Vec2 applyPoint, float blastPower) {
		// ignore any non-dynamic bodies
		if (body.getType() != BodyType.DYNAMIC)
			return;
		Vec2 blastDir = new Vec2(applyPoint.add(blastCenter.negate()));
		float distance = blastDir.normalize();
		// ignore bodies exactly at the blast point - blast direction is
		// undefined
		if (distance == 0)
			return;
		float invDistance = 1 / distance;
		float impulseMag = blastPower * invDistance * invDistance;
		impulseMag = Math.min(impulseMag, 500.0f);
		body.applyLinearImpulse(blastDir.mul(impulseMag), applyPoint);
	}

	private void explode_proximity() {

		timer.stop();
		if (hasExplosed.getAndSet(true)) {
			throw new IllegalStateException("Bomb has already exploded");
		}
		Vec2 center = getPosition();

		// find all fixtures within blast radius AABB
		MyQueryCallback queryCallback = new MyQueryCallback();
		Vec2 vec = new Vec2(Math.round(BLAST_RADIUS), Math.round(BLAST_RADIUS));
		AABB aabb = new AABB(center.add(vec.negate()), center.add(vec));
		getWorld().queryAABB(queryCallback, aabb);

		// check which of these have their center of mass within the blast
		// radius
		for (int i = 0; i < queryCallback.foundBodies.size(); i++) {
			Body body = queryCallback.foundBodies.pollFirst();
			Vec2 bodyCom = body.getWorldCenter();
			// ignore bodies outside the blast range
			if (new Vec2(bodyCom.add(center.negate())).length() >= BLAST_RADIUS)
				continue;
			applyBlastImpulse(body, center, bodyCom, BLAST_POWER * 0.05f);
		}
	}

	public static Bomb create(World world, Vec2 vec2) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(vec2.x, vec2.y);
		bodyDef.type = BodyType.STATIC;
		bodyDef.awake = true;
		Body body = world.createBody(bodyDef);
		return new Bomb(body);
	}

	private static FixtureDef getFixtureDef() {
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setRadius(.2f);
		dynamicBox.setAsBox(.4f, .4f);
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		return fixtureDef;
	}

	@Override
	public Shape getGraphicShape() {
		if (hasExplosed.get()) {
			return new Ellipse2D.Float(0, 0, 0, 0);
		}
		Point position = getGraphicPosition();
		float radius = getRadius();
		int radiusX = Math.abs(Graphics.gameToGraphicX(radius));
		int radiusY = Math.abs(Graphics.gameToGraphicY(radius));
		return new Ellipse2D.Float(position.x - radiusX, position.y - radiusY,
				radiusX * 2, radiusY * 2);
	}

}
