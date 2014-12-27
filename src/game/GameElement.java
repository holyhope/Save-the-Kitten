package game;

import java.awt.Graphics2D;
import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

public abstract class GameElement {
	/**
	 * Body of the element
	 */
	private final Body body;

	/**
	 * Create en element with a body.
	 * 
	 * @param body
	 *            of the element
	 */
	public GameElement(Body body) {
		this.body = Objects.requireNonNull(body);
		body.setUserData(this);
	}

	/**
	 * Get the position of the element in the round
	 * 
	 * @return
	 */
	public Vec2 getPosition() {
		return body.getPosition();
	}

	/**
	 * Enable or disable element.
	 * 
	 * @param active
	 *            True to enable element
	 */
	public void setActive(boolean active) {
		body.setActive(active);
	}

	/**
	 * Check if element is active.
	 * 
	 * @return True if element is enabled.
	 */
	public boolean isActive() {
		return body.isActive();
	}

	/**
	 * Check if element is in a world.
	 * 
	 * @param world
	 *            to check
	 * @return True if element is in world.
	 */
	public boolean isInWorld(World world) {
		return body.getWorld().equals(world);
	}

	/**
	 * Get the world of the element.
	 * 
	 * @return World of the element.
	 */
	public World getWorld() {
		return body.getWorld();
	}

	/**
	 * Set velocity to the element
	 * 
	 * @param velocity
	 *            to set
	 */
	public void setLinearVelocity(Vec2 velocity) {
		body.setLinearVelocity(velocity);
	}

	/**
	 * Draw element in graphics.
	 * 
	 * @param graphics
	 */
	public abstract void draw(Graphics2D graphics);
}