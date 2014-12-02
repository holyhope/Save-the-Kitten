import javax.swing.JWindow;

public class Cat implements GameElement {
	private final String name;
	private static int nbCat = 0;
	
	public Cat(String name) {
		this.name = name;
	}
	
	public Cat() {
		this("Unamed cat (" + nbCat + ")");
		nbCat++;
	}

	@Override
	public void draw(JWindow window) {
		// TODO Auto-generated method stub
	}

	public boolean isLaunched() {
		return false;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
