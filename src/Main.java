import game.Game;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Main {
	public static void main(String[] args) {
		Game game = new Game();
		game.runApplication();
	}

	/*
	 * World world = new World(new Vec2(0, 0)); Launcher launcher =
	 * Launcher.create(world, new Point(5, 5), 3, new Vec2(0.1f,0)); Round round
	 * = Round.create(new Dimension(30, 30), launcher);
	 * round.add(Goal.create(world, new Point(2, 2)));
	 * round.add(Goal.create(world, new Point(3, 2)));
	 * round.add(Goal.create(world, new Point(2, 3))); round.start(); if (
	 * round.isVictory() ) { System.out.println("Victory !"); } else if (
	 * round.isDefeat() ) { System.out.println("Defeat !"); }
	 */

	public static void jBox2DExample(String[] args) {
		Vec2 gravity = new Vec2(0, -10);
		World world = new World(gravity);
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(0, -10);
		Body groundBody = world.createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(50, 10);
		groundBody.createFixture(groundBox, 0);

		// Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(0, 4);
		Body body = world.createBody(bodyDef);
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		body.createFixture(fixtureDef);

		// Setup world
		float timeStep = 1.0f / 60.0f;
		int velocityIterations = 6;
		int positionIterations = 2;

		// Run loop
		for (int i = 0; i < 60; ++i) {
			world.step(timeStep, velocityIterations, positionIterations);
			Vec2 position = body.getPosition();
			float angle = body.getAngle();
			System.out.printf("%4.2f %4.2f %4.2f\n", position.x, position.y,
					angle);
		}

	}

}
