package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

import fr.umlv.zen4.ApplicationContext;
import fr.umlv.zen4.MotionEvent;
import fr.umlv.zen4.MotionEvent.Action;
import fr.umlv.zen4.ScreenInfo;

public class Graphics {
	/**
	 * Width of the panel
	 */
	public static final ThreadLocal<Integer> WIDTH = new ThreadLocal<Integer>();
	/**
	 * Height of the panel
	 */
	public static final ThreadLocal<Integer> HEIGHT = new ThreadLocal<Integer>();
	/**
	 * First left pixel of the panel in the window
	 */
	public static final ThreadLocal<Integer> LEFT_PIXEL = new ThreadLocal<Integer>();
	/**
	 * First top pixel of the panel in the window
	 */
	public static final ThreadLocal<Integer> TOP_PIXEL = new ThreadLocal<Integer>();
	/**
	 * Width of the window
	 */
	private static final ThreadLocal<Integer> REAL_WIDTH = new ThreadLocal<Integer>();
	/**
	 * Height of the window
	 */
	private static final ThreadLocal<Integer> REAL_HEIGHT = new ThreadLocal<Integer>();
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
	public static final long DEFINITION = 200;
	/**
	 * Time before clear exception text
	 */
	private static final long EXCEPTION_TIME = 1000;
	/**
	 * Store exception actually displayed into each context
	 */
	private static final ThreadLocal<Throwable> exceptions = new ThreadLocal<>();

	/**
	 * Write text centered on windows
	 * 
	 * @param graphics2D
	 *            where to paint text
	 * @param string
	 *            text to paint
	 */
	public static void writeTextCentered(Graphics2D g, String string) {
		writeTextCentered(g, string, WIDTH.get() / 2, HEIGHT.get() / 2);
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
		graphics2D.setTransform(AffineTransform.getTranslateInstance(
				LEFT_PIXEL.get(), TOP_PIXEL.get()));
		graphics2D.drawString(string, x - fontMetrics.stringWidth(string) / 2,
				y - fontMetrics.getHeight() / 2);
	}

	/**
	 * Draw grid on each step pixel.
	 * 
	 * @param graphics
	 *            to draw in.
	 * @param step
	 *            between two line.
	 * @param j
	 * @param height2
	 * @param width2
	 * @param y
	 */
	public static void drawGrid(Graphics2D graphics, int x, int y, int width,
			int height, int step) {
		graphics.setColor(Color.LIGHT_GRAY);
		for (int i = x; i < x + width; i += step) {
			graphics.drawLine(i, y, i, y + height);
		}
		for (int i = y; i < y + height; i += step) {
			graphics.drawLine(x, i, x + width, i);
		}
	}

	/**
	 * Show round into full window
	 * 
	 * @param graphics2D
	 *            where to paint round
	 * @param round
	 *            actual Round to paint
	 */
	public static void update(Graphics2D graphics2D, Round round) {
		update(graphics2D, round, LEFT_PIXEL.get(), TOP_PIXEL.get(),
				WIDTH.get(), HEIGHT.get());
	}

	/**
	 * 
	 * Show round into area defined by (x,y,width,height).
	 * 
	 * @param graphics2D
	 *            where to paint round
	 * @param round
	 *            actual Round to paint
	 * @param x
	 *            first left pixel coordinate.
	 * @param y
	 *            first top pixel coordinate.
	 * @param width
	 *            of the area.
	 * @param height
	 *            of the area.
	 */
	public static void update(Graphics2D graphics2D, Round round, int x, int y,
			int width, int height) {
		Dimension dimension = new Dimension(Math.round(DEFINITION
				* round.getWidth()), Math.round(DEFINITION * round.getHeight()));
		BufferedImage image = new BufferedImage(dimension.width,
				dimension.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();

		graphics.translate(0, dimension.getHeight());

		graphics.setColor(Color.DARK_GRAY);

		round.draw(graphics);

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
		graphics.setColor(Color.RED);
		for (GameElement element : round.getBombs()) {
			element.draw(graphics);
		}

		/*
		 * graphics2D.setBackground(Graphics.BACKGROUND_COLOR);
		 * graphics2D.clearRect(x, y, width, height); drawGrid(graphics2D, x, y,
		 * width, height, 15);
		 */
		graphics2D.setTransform(AffineTransform.getTranslateInstance(x, y));
		graphics2D.drawImage(image, AffineTransform.getScaleInstance(width
				/ dimension.getWidth(), height / dimension.getHeight()), null);
		graphics2D.setTransform(AffineTransform.getTranslateInstance(0, 0));
	}

	/**
	 * Convert value from jbox2d to zen4
	 * 
	 * @param value
	 *            from jbox2d
	 * @return new value
	 */
	public static int gameToGraphicY(float value) {
		return -Math.round(value * DEFINITION);
	}

	/**
	 * Convert value from jbox2d to zen4
	 * 
	 * @param value
	 *            from jbox2d
	 * @return new value
	 */
	public static int gameToGraphicX(float value) {
		return Math.round(value * DEFINITION);
	}

	/**
	 * Display exception error message in context
	 * 
	 * @param exception
	 *            to report
	 */
	public static void addException(Throwable exception) {
		exception.printStackTrace();
		exceptions.set(exception);
	}

	public static void displayException(Graphics2D graphics2D) {
		int y = 10;
		Throwable exception = exceptions.get();
		if (exception == null) {
			return;
		}
		graphics2D.setColor(Color.RED);
		graphics2D.setFont(new Font("Courier New", 0, 12));
		FontMetrics fontMetrics = graphics2D.getFontMetrics();
		String message = exception.getLocalizedMessage();
		graphics2D.drawString(message,
				WIDTH.get() - fontMetrics.stringWidth(message), y);
	}

	/**
	 * Fill background and draw area.
	 * 
	 * @param graphics2D
	 *            to draw in.
	 */
	public static void drawBackground(Graphics2D graphics2D) {
		graphics2D.setColor(Color.BLACK);
		graphics2D.fillRect(0, 0, Graphics.REAL_WIDTH.get(),
				Graphics.REAL_HEIGHT.get());
		graphics2D.setBackground(BACKGROUND_COLOR);
		graphics2D.setTransform(AffineTransform.getTranslateInstance(
				LEFT_PIXEL.get(), TOP_PIXEL.get()));
		graphics2D.clearRect(0, 0, WIDTH.get(), HEIGHT.get());
		displayException(graphics2D);
	}

	/**
	 * Initialize all parameters
	 * 
	 * @param context
	 *            of the application
	 */
	public static void init(ApplicationContext context) {
		ScreenInfo screenInfo = context.getScreenInfo();
		int width = Math.round(screenInfo.getWidth());
		int height = Math.round(screenInfo.getHeight());
		int min = Math.min(width, height);
		REAL_WIDTH.set(width);
		REAL_HEIGHT.set(height);
		WIDTH.set(min);
		HEIGHT.set(min);
		LEFT_PIXEL.set((width - min) / 2);
		TOP_PIXEL.set((height - min) / 2);
	}

	/**
	 * Check if user left click in area defined by (x,y,width,height).
	 * 
	 * @param context
	 *            of the application.
	 * @param event
	 *            of the mouse.
	 * @param x
	 *            of the top left corner of the area.
	 * @param y
	 *            of the top left corner of the area.
	 * @param width
	 *            of the area.
	 * @param height
	 *            of the area.
	 * @return
	 */
	public static boolean click(ApplicationContext context, MotionEvent event,
			int x, int y, int width, int height) {
		float mouseX = event.getX();
		float mouseY = event.getY();
		return Action.DOWN.equals(Objects.requireNonNull(event).getAction())
				&& x <= mouseX && mouseX < x + width && y <= mouseY
				&& mouseY < y + height;
	}
}
