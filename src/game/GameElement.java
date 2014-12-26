package game;
import java.awt.Graphics2D;
import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

public abstract class GameElement {
	private final Body body;

	public GameElement(Body body) {
		this.body = Objects.requireNonNull(body);
		body.setUserData(this);
	}

	public Vec2 getPosition() {
		return body.getPosition();
	}
	
	public void setActive(boolean active) {
		body.setActive(active);
	}
	
	public boolean isActive() {
		return body.isActive();
	}
	
	public boolean isInWorld(World world) {
		return body.getWorld().equals(world);
	}

	public World getWorld() {
		return body.getWorld();
	}

	public void setLinearVelocity(Vec2 vec2) {
		// TODO Auto-generated method stub
		
	}

	public abstract void draw(Graphics2D g);
}