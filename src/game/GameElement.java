package game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
	 * Get the real position of the element in the round
	 * 
	 * @return Position of the element.
	 */
	public Vec2 getPosition() {
		return body.getPosition();
	}

	/**
	 * Get the position of the element graphically.
	 * 
	 * @return Graphical position of the element.
	 */
	public Point getGraphicPosition() {
		Vec2 position = body.getPosition();
		return new Point(Graphics.gameToGraphicX(position.x),
				Graphics.gameToGraphicY(position.y));
	}

	/**
	 * Get the graphic shape.
	 * 
	 * @return Shape of the element
	 */
	public abstract Shape getGraphicShape();

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
	 * Get the radius of the element.
	 * 
	 * @return radius of the element.
	 */
	public float getRadius() {
		try {
			return body.getFixtureList().m_shape.m_radius;
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
	 * Transform shape with element's data (rotation).
	 * 
	 * @param shape
	 *            to transform.
	 * @return new shape transformed.
	 */
	public Shape transformShape(Shape shape) {
		Point position = getGraphicPosition();
		return AffineTransform.getRotateInstance(body.getAngle(), position.x,
				position.y).createTransformedShape(shape);
	}

	/**
	 * Draw element in graphics.
	 * 
	 * @param graphics
	 */
	public void draw(Graphics2D graphics) {
		Shape shape = getGraphicShape();
		if (shape != null) {
			graphics.fill(shape);
		}
	}
}