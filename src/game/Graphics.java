package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Graphics {
	/**
	 * Width of the panel
	 */
	public static final int WIDTH = 800;
	/**
	 * Height of the panel
	 */
	public static final int HEIGHT = 800;
	/**
	 * Background color of the panel
	 */
	public static final Color BACKGROUND_COLOR = Color.WHITE;
	/**
	 * Time between 2 panel's update
	 */
	public static final long REFRESH_TIME = 2;
	/**
	 * Use for rounded position
	 */
	public static final long PRECISION = 100;

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

	private static void drawGrid(Graphics2D graphics, Round round) {
		int graphicWidth = gameToGraphicX(round.getWidth());
		int graphicHeight = gameToGraphicY(round.getHeight());
		int graphicValue;
		for (float i = 0; i < round.getWidth(); i = i + 0.1f) {
			graphicValue = gameToGraphicX(i);
			graphics.drawLine(graphicValue, 0, graphicValue, graphicHeight);
			graphicValue = gameToGraphicY(i);
			graphics.drawLine(0, graphicValue, graphicWidth, graphicValue);
		}
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
		Dimension dimension = new Dimension(Math.round(PRECISION
				* round.getWidth()), Math.round(PRECISION * round.getHeight()));
		BufferedImage image = new BufferedImage(dimension.width,
				dimension.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setBackground(Graphics.BACKGROUND_COLOR);
		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

		graphics.translate(0, dimension.getHeight());

		graphics.setColor(Color.LIGHT_GRAY);
		drawGrid(graphics, round);

		graphics.setColor(Color.GREEN);
		round.getLaunchers().stream().forEach(e -> e.draw(graphics));

		graphics.setColor(Color.BLUE);
		round.getGoals().stream().forEach(e -> e.draw(graphics));

		graphics.setColor(Color.BLUE);
		round.getBullets().stream().forEach(e -> e.draw(graphics));

		graphics2D.drawImage(image, AffineTransform.getScaleInstance(WIDTH
				/ dimension.getWidth(), HEIGHT / dimension.getHeight()), null);
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static int gameToGraphicY(float value) {
		return -Math.round(value * PRECISION);
	}

	public static int gameToGraphicX(float value) {
		return Math.round(value * PRECISION);
	}
}
