package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

import fr.umlv.zen3.ApplicationContext;

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
	public static final long DEFINITION = 500;
	/**
	 * Time before clear exception text
	 */
	private static final long EXCEPTION_TIME = 1000;
	/**
	 * Store exception actually displayed into each context
	 */
	private static final ConcurrentHashMap<ApplicationContext, Throwable> exceptions = new ConcurrentHashMap<>();

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
		Dimension dimension = new Dimension(Math.round(DEFINITION
				* round.getWidth()), Math.round(DEFINITION * round.getHeight()));
		BufferedImage image = new BufferedImage(dimension.width,
				dimension.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setBackground(Graphics.BACKGROUND_COLOR);
		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

		graphics.translate(0, dimension.getHeight());

		graphics.setColor(Color.LIGHT_GRAY);
		drawGrid(graphics, round);

		graphics.setColor(Color.GREEN);
		for (GameElement element : round.getLaunchers()) {
			element.draw(graphics);
		}

		graphics.setColor(Color.BLUE);
		for (GameElement element : round.getGoals()) {
			element.draw(graphics);
		}

		graphics.setColor(Color.BLACK);
		for (GameElement element : round.getBullets()) {
			element.draw(graphics);
		}

		graphics2D.drawImage(image, AffineTransform.getScaleInstance(WIDTH
				/ dimension.getWidth(), HEIGHT / dimension.getHeight()), null);
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static int gameToGraphicY(float value) {
		return -Math.round(value * DEFINITION);
	}

	public static int gameToGraphicX(float value) {
		return Math.round(value * DEFINITION);
	}

	public static void addException(ApplicationContext context, Throwable t) {
		Throwable old = exceptions.put(context, t);
		if (old != null) {
			context.render(g -> {
				hideException(g, old);
			});
		}
		if (t != null) {
			context.render(g -> {
				displayException(g, t);
			});
		}
	}

	private static void hideException(Graphics2D g, Throwable t) {
		int y = 10;
		FontMetrics fontMetrics = g.getFontMetrics();
		String message = t.getLocalizedMessage();
		g.setBackground(BACKGROUND_COLOR);
		g.clearRect(WIDTH - fontMetrics.stringWidth(message), y, WIDTH, y
				+ fontMetrics.getHeight());
	}

	private static void displayException(Graphics2D g, Throwable t) {
		int y = 10;
		g.setColor(Color.RED);
		g.setFont(new Font("Courier New", 0, 10));
		FontMetrics fontMetrics = g.getFontMetrics();
		String message = t.getLocalizedMessage();
		g.drawString(message, WIDTH - fontMetrics.stringWidth(message), y);
	}
}
