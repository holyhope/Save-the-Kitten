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

		bulletBeginContact(bodyA.getUserData(), bodyB);
		bulletBeginContact(bodyB.getUserData(), bodyA);
	}

	/**
	 * Call bullet.beginContact(body) if bullet is effectively a Bullet
	 * @param bullet
	 * @param body
	 */
	private void bulletBeginContact(Object bullet, Body body) {
		if (!(bullet instanceof Bullet)) {
			return;
		}
		((Bullet) bullet).beginContact(body);
	}

	@Override
	public void endContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();

		bulletEndContact(bodyA.getUserData(), bodyB);
		bulletEndContact(bodyB.getUserData(), bodyA);
	}

	/**
	 * Call bullet.endContact(body) if bullet is effectively a Bullet
	 * @param bullet
	 * @param body
	 */
	private void bulletEndContact(Object bullet, Body body) {
		if (!(bullet instanceof Bullet)) {
			return;
		}
		((Bullet) bullet).endContact(body);
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
