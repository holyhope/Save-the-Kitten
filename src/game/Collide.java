package game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

public class Collide implements ContactListener {
	@Override
	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();

		System.out.println("collision: " + bodyA.getUserData() + " - "
				+ bodyB.getUserData());

		BulletBeginContact(bodyA.getUserData(), bodyB);
		BulletBeginContact(bodyB.getUserData(), bodyA);
	}

	private void BulletBeginContact(Object bullet, Body body) {
		if (!(bullet instanceof Bullet)) {
			return;
		}
		((Bullet) bullet).beginContact(body);
	}

	@Override
	public void endContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();

		System.out.println("collision: " + bodyA.getUserData() + " - "
				+ bodyB.getUserData());

		BulletEndContact(bodyA.getUserData(), bodyB);
		BulletEndContact(bodyB.getUserData(), bodyA);
	}

	private void BulletEndContact(Object bullet, Body body) {
		if (!(bullet instanceof Bullet)) {
			return;
		}
		((Bullet) bullet).beginContact(body);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
