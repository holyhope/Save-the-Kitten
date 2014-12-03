import javax.swing.JWindow;

public interface GameElement {
	public default boolean isVictory() {
		return true;
	}

	public void draw(JWindow window);
}