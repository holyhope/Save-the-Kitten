package game;

import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

/**
 * 
 * @author PERONNET Pierre
 * @author PICHOU Maxime
 */
public class Vortex extends Bomb {
	private Vortex(Body body) {
		super(body);
	}

	@Override
	protected void applyBlastImpulse(Body body, Vec2 blastCenter,
			Vec2 applyPoint, float blastPower) {
		super.applyBlastImpulse(body, blastCenter, applyPoint, -blastPower);
	}

	public static Vortex create(World world, Vec2 position) {
		BodyDef bodyDef = new BodyDef();
		Objects.requireNonNull(position);
		bodyDef.position.set(position.x, position.y);
		bodyDef.type = BodyType.STATIC;
		bodyDef.awake = true;
		Body body = Objects.requireNonNull(world).createBody(bodyDef);
		return new Vortex(body);
	}
}
