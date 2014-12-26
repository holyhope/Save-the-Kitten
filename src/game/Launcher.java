package game;

import java.awt.Graphics2D;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Launcher extends GameElement {
	private final long waitTime = 1500;
	private final Vec2 orientation;
	private final int nbCat;
	private boolean stop = false;

	private Launcher(Body body, Vec2 position, int nbCat, Vec2 orientation) {
		super(body);
		if (nbCat <= 0) {
			throw new IllegalArgumentException(
					"Le nombre de chat doit etre positif.");
		}
		Objects.requireNonNull(position);
		Objects.requireNonNull(orientation);
		this.orientation = new Vec2(orientation.x, orientation.y);
		this.nbCat = nbCat;
	}

	public static Launcher create(World world, Vec2 position, float strengh) {
		return create(world, position, 1, new Vec2(strengh, 0));
	}

	public static Launcher create(World world, Vec2 vec2, int nbCat,
			Vec2 orientation) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.active = true;
		Objects.requireNonNull(vec2);
		Vec2 orientationNormalized = new Vec2();
		orientationNormalized.set(orientation).normalize();
		bodyDef.angle = (float) Math.acos(Vec2.dot(new Vec2(1, 0),
				orientationNormalized));
		bodyDef.position.set(vec2.x, vec2.y);
		Body body = Objects.requireNonNull(world).createBody(bodyDef);
		Launcher launcher = new Launcher(body, vec2, nbCat, orientation);
		return launcher;
	}

	public void stopLaunch() {
		stop = true;
	}

	public Set<Bullet> launch() {
		final LinkedHashSet<Bullet> set = new LinkedHashSet<>();
		Random random = new Random();
		for (int i = 0; i < nbCat; i++) {
			Vec2 position = getPosition();
			set.add(Cat.create(getWorld(), new Vec2(Math.round(position.x),
					Math.round(position.y)), new Vec2(orientation), random
					.nextFloat()));
		}

		final LinkedHashSet<Bullet> setFinal = new LinkedHashSet<>();
		setFinal.addAll(set);
		new Thread(() -> {
			for (Bullet bullet : setFinal) {
				if (stop) {
					return;
				}
				bullet.start();
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		return set;
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}
}
