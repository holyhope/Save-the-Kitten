package game;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Graphics {
	/**
	 * Width of the panel
	 */
	public static final int WIDTH = 1024;
	/**
	 * Height of the panel
	 */
	public static final int HEIGHT = 768;
	/**
	 * Background color of the panel
	 */
	public static final Color BACKGROUND_COLOR = Color.WHITE;
	/**
	 * Time between 2 panel's update
	 */
	public static final long REFRESH_TIME = 60;
	/**
	 * Use for rounded position
	 */
	public static final int PRECISION = 100;

	/**
	 * Write text centered on windows
	 * 
	 * @param graphics2D
	 *            where to paint text
	 * @param string
	 *            text to paint
	 */
	public static void writeTextCentered(Graphics2D g, String string) {
		writeTextCentered(g, string, WIDTH / 2, HEIGHT / 2);
	}

	/**
	 * Write text centered on (x;y)
	 * 
	 * @param graphics2D
	 *            where to paint text
	 * @param string
	 *            text to paint
	 * @param x
	 *            of the center coordinate
	 * @param y
	 *            of the center coordinate
	 */
	public static void writeTextCentered(Graphics2D graphics2D, String string,
			int x, int y) {
		FontMetrics fontMetrics = graphics2D.getFontMetrics();
		graphics2D.drawString(string, x - fontMetrics.stringWidth(string) / 2,
				y - fontMetrics.getHeight() / 2);
	}

	/**
	 * Show round into graphics2D
	 * 
	 * @param graphics2D
	 *            where to paint round
	 * @param round
	 *            actual Round to paint
	 */
	public static void update(Graphics2D graphics2D, Round round) {
		BufferedImage image = new BufferedImage(Math.round(PRECISION
				* round.getWidth()), Math.round(PRECISION * round.getHeight()),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setBackground(BACKGROUND_COLOR);
		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

		graphics.setColor(Color.GREEN);
		round.getLaunchers().stream().forEach(e -> e.draw(graphics));

		graphics.setColor(Color.BLUE);
		round.getGoals().stream().forEach(e -> e.draw(graphics));

		graphics.setColor(Color.BLUE);
		round.getBullets().stream().forEach(e -> e.draw(graphics));

		graphics.scale(WIDTH / (PRECISION * round.getWidth()), HEIGHT
				/ (PRECISION * round.getHeight()));

		graphics2D.drawImage(image, null, 0, 0);
	}
}
