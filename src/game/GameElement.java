package game;
import java.util.Objects;

import javax.swing.JWindow;

import org.jbox2d.dynamics.Body;

public abstract class GameElement {
	private final Body body;

	public GameElement(Body body) {
		this.body = Objects.requireNonNull(body);
	}

	public Body getBody() {
		return body;
	}

	public abstract void draw(JWindow window);
}