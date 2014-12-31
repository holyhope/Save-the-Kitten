package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.umlv.zen4.Application;
import fr.umlv.zen4.ApplicationContext;
import fr.umlv.zen4.MotionEvent;
import fr.umlv.zen4.MotionEvent.Action;

public class Game {
	private static final int ROUNDS_DISPLAYED = 4;
	private static final int NUMBER_DEFAULT_ROUND = 2;
	private static final Round defaultRounds[] = new Round[NUMBER_DEFAULT_ROUND];
	private final ArrayList<Round> rounds = new ArrayList<>();

	private Game() {
	}

	/**
	 * Create a game and initialize default rounds.
	 * 
	 * @return new Game
	 */
	public static Game create() {
		Game game = new Game();
		game.rounds.addAll(Arrays.asList(defaultRounds).stream()
				.filter(r -> r != null).collect(Collectors.toList()));
		return game;
	}

	static {
		for (int i = 0; i < defaultRounds.length; i++) {
			try {
				defineRound(i);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	public static Round round0() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 5f, 5f);
		Launcher launcher = Launcher.create(world, new Vec2(3, 2), 1, new Vec2(
				-.001f, .001f));
		launcher.addBullet(Cat.class);
		round.add(launcher);
		round.add(Goal.create(world, new Vec2(0, 3)));
		round.add(Bomb.class);

		return round;
	}

	public static Round round1() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 5f, 5f);
		Launcher launcher = Launcher.create(world, new Vec2(2, 2), 1, new Vec2(
				.001f, -.001f));
		launcher.addBullet(Cat.class);
		round.add(launcher);
		round.add(Goal.create(world, new Vec2(3, 3)));
		round.add(Goal.create(world, new Vec2(2, 3)));
		round.createWall(2.4f, 1.5f, 1.2f, 1f);
		round.add(Bomb.class);
		round.add(Bomb.class);
		return round;
	}

	/**
	 * Define default round represented by index.
	 * 
	 * @param index
	 *            of the round to define
	 * @throws IllegalStateException
	 */
	private static void defineRound(int index) throws IllegalStateException {
		Round round = null;
		try {
			switch (index) {
			case 0:
				round = round0();
				break;
			case 1:
				round = round1();
				break;
			default:
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new IllegalStateException(e);
		}
		if (round == null) {
			throw new IllegalStateException("No round " + index + " found");
		}
		defaultRounds[index] = round;
	}

	/**
	 * Add round to Game. User can select it thanks to the round selector.
	 * 
	 * @param round
	 *            to add
	 */
	public void addRound(Round round) {
		rounds.add(Objects.requireNonNull(round));
	}

	/**
	 * Draw round selector for getRound()
	 * 
	 * @param graphics2D
	 *            to draw in
	 * @param page
	 *            displayed
	 */
	private void drawRoundSelector(Graphics2D graphics2D, int page) {
		int deltaPage = page * ROUNDS_DISPLAYED;
		int width = (Graphics.WIDTH.get() * 2) / ROUNDS_DISPLAYED;
		int height = Graphics.HEIGHT.get() / 2;
		int halfRoundDisplayed = ROUNDS_DISPLAYED / 2;
		int separatorSize = 6;
		int halfSeparatorSize = separatorSize / 2;
		int doubleHeight = height * 2;

		graphics2D.setBackground(Graphics.BACKGROUND_COLOR);
		for (int i = 0; i < halfRoundDisplayed; i++) {
			int x = Graphics.LEFT_PIXEL.get() + i * width;
			int y = Graphics.TOP_PIXEL.get();
			try {
				Round round = rounds.get(deltaPage + i);
				graphics2D.clearRect(x, y, width, height);
				Graphics.update(graphics2D, round, x, y, width, height);
			} catch (Exception e) {

			}
			try {
				Round round = rounds.get(deltaPage + i + halfRoundDisplayed);
				graphics2D.clearRect(x, y, width, height);
				Graphics.update(graphics2D, round, x, y, width, height);
			} catch (Exception e) {

			}
			graphics2D.setColor(Color.BLACK);
			graphics2D.fillRect(x - halfSeparatorSize, y, separatorSize,
					doubleHeight);
			graphics2D.fillRect(x + width - halfSeparatorSize, y,
					separatorSize, doubleHeight);
		}
		graphics2D.setColor(Color.BLACK);
		graphics2D.fillRect(Graphics.LEFT_PIXEL.get(), Graphics.TOP_PIXEL.get()
				+ height - halfSeparatorSize, Graphics.WIDTH.get(),
				separatorSize);
		drawRoundSelectorCurrentPage(graphics2D, page);
		drawRoundSelectorArrows(graphics2D, page > 1,
				page < Math.floor(rounds.size() / ROUNDS_DISPLAYED));
	}

	/**
	 * Draw page number in window
	 * 
	 * @param graphics2D
	 *            to draw in
	 * @param page
	 *            number to display
	 */
	private void drawRoundSelectorCurrentPage(Graphics2D graphics2D, int page) {
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(new Font("Verdana", 0, 15));
		graphics2D.drawString("page " + (1 + page) + "/"
				+ (1 + rounds.size() / ROUNDS_DISPLAYED), 10, 15);
	}

	/**
	 * Draw arrow to navigate.
	 * 
	 * @param graphics2D
	 *            to draw in
	 * @param previous
	 *            must be true to display previous arrows.
	 * @param next
	 *            must be true to display next arrows.
	 */
	private void drawRoundSelectorArrows(Graphics2D graphics2D,
			boolean previous, boolean next) {
		final int arrowSpace = 20;
		final int arrowSize = 30;
		Shape arrow = new Polygon();
		((Polygon) arrow).addPoint(0, 0);
		((Polygon) arrow).addPoint(arrowSize, arrowSize / 2);
		((Polygon) arrow).addPoint(0, arrowSize);

		if (next) {
			// Right arrow
			graphics2D.setTransform(AffineTransform.getTranslateInstance(
					Graphics.LEFT_PIXEL.get() + Graphics.WIDTH.get()
							+ arrowSpace, Graphics.TOP_PIXEL.get()
							+ (Graphics.HEIGHT.get() - arrowSize) / 2));
			graphics2D.fill(arrow);
			// Bottom arrow
			graphics2D.setTransform(AffineTransform.getTranslateInstance(
					Graphics.LEFT_PIXEL.get()
							+ (Graphics.WIDTH.get() - arrowSize) / 2,
					Graphics.TOP_PIXEL.get() + Graphics.HEIGHT.get()
							+ arrowSize));
			graphics2D.fill(AffineTransform.getRotateInstance(Math.PI / 2, 0,
					arrowSize / 2).createTransformedShape(arrow));
		}

		if (previous) {
			// Left arrow
			graphics2D.setTransform(AffineTransform.getTranslateInstance(
					Graphics.LEFT_PIXEL.get() - arrowSpace,
					Graphics.TOP_PIXEL.get()
							+ (Graphics.HEIGHT.get() - arrowSize) / 2));
			graphics2D.fill(AffineTransform.getRotateInstance(Math.PI, 0,
					arrowSize / 2).createTransformedShape(arrow));
			// Top arrow
			graphics2D.setTransform(AffineTransform.getTranslateInstance(
					Graphics.LEFT_PIXEL.get()
							+ (Graphics.WIDTH.get() - arrowSize) / 2,
					Graphics.TOP_PIXEL.get() - arrowSpace));
			graphics2D.fill(AffineTransform.getRotateInstance(-Math.PI / 2, 0,
					arrowSize / 2).createTransformedShape(arrow));
		}

	}

	/**
	 * Let user select a round.
	 * 
	 * @param context
	 *            of the current game.
	 * @return new Round chosen by user.
	 */
	private Round getRound(ApplicationContext context) {
		final long waitBetweenClick = 100;
		if (rounds.isEmpty()) {
			throw new IllegalStateException("No round to select");
		}

		int width = (Graphics.WIDTH.get() * 2) / ROUNDS_DISPLAYED;
		int height = Graphics.HEIGHT.get() / 2;
		int halfRoundDisplayed = ROUNDS_DISPLAYED / 2;

		int page = 0;
		boolean pageChanged = true;
		long lastPageChanged = System.currentTimeMillis();
		for (;;) {
			if (pageChanged) {
				pageChanged = false;
				final int ActualPage = page;
				context.renderFrame((g, contentLost) -> {
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, Graphics.WIDTH.get(),
							Graphics.HEIGHT.get());
					drawRoundSelector(g, ActualPage);
				});
			}
			try {
				MotionEvent event = context.waitAndBlockUntilAMotion();
				int deltaPage = page * ROUNDS_DISPLAYED;
				for (int i = 0; i < halfRoundDisplayed; i++) {
					int x = Graphics.LEFT_PIXEL.get() + i * width;
					int y = Graphics.TOP_PIXEL.get();
					if (Graphics.click(context, event, x, y, width, height)) {
						try {
							return rounds.get(deltaPage + i);
						} catch (IndexOutOfBoundsException e) {
						}
					} else if (Graphics.click(context, event, x, y + height,
							width, height)) {
						try {
							return rounds.get(deltaPage + i
									+ halfRoundDisplayed);
						} catch (IndexOutOfBoundsException e) {
						}
					} else if (System.currentTimeMillis() - lastPageChanged > waitBetweenClick) {
						if (Graphics
								.click(context,
										event,
										Graphics.LEFT_PIXEL.get()
												+ Graphics.WIDTH.get(),
										Graphics.TOP_PIXEL.get(),
										Integer.MAX_VALUE
												- (Graphics.LEFT_PIXEL.get() + Graphics.WIDTH
														.get()),
										Graphics.HEIGHT.get())
								|| Graphics
										.click(context,
												event,
												Graphics.LEFT_PIXEL.get(),
												Graphics.TOP_PIXEL.get()
														+ Graphics.HEIGHT.get(),
												Graphics.WIDTH.get(),
												Integer.MAX_VALUE
														- (Graphics.TOP_PIXEL
																.get() + Graphics.HEIGHT
																.get()))) {
							if (page < Math.floor(rounds.size()
									/ ROUNDS_DISPLAYED)) {
								page++;
								pageChanged = true;
							}
							lastPageChanged = System.currentTimeMillis();
						} else if (Graphics.click(
								context,
								event,
								0,
								0,
								Graphics.LEFT_PIXEL.get(),
								Graphics.TOP_PIXEL.get()
										+ Graphics.HEIGHT.get())
								|| Graphics.click(context, event, 0, 0,
										Graphics.LEFT_PIXEL.get()
												+ Graphics.WIDTH.get(),
										Graphics.TOP_PIXEL.get())) {
							if (page > 1) {
								page--;
								pageChanged = true;
							}
							lastPageChanged = System.currentTimeMillis();
						}
					}
				}
			} catch (InterruptedException e) {
				Graphics.addException(e);
			}
		}
	}

	/**
	 * Add a bomb at position pointed by event.
	 * 
	 * @param round
	 *            where to place the bomb.
	 * @param event
	 *            pointing the position.
	 * @return Bomb planted.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Bomb plantBomb(Round round, MotionEvent event)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Point pos = new Point(Math.round(event.getX()),
				Math.round(event.getY()));
		Vec2 position = new Vec2(Graphics.graphicXToGame(round, pos.x),
				Graphics.graphicYToGame(round, pos.y));
		return round.plantNextBomb(position);
	}

	/**
	 * Increase timer.
	 * 
	 * @param bomb bomb to change
	 * @param timer
	 *            - actual timer.
	 * @return new timer.
	 */
	private int increaseTimer(Bomb bomb, int timer) {
		timer = timer % bomb.getMaxTimer() + bomb.getTimerPrecision();
		bomb.setTimer(timer);
		return timer;
	}

	/**
	 * Get initial timer's value.
	 * 
	 * @return new timer.
	 */
	private int resetTimer(Bomb bomb) {
		bomb.setTimer(bomb.getTimerPrecision());
		return bomb.getTimerPrecision();
	}

	/**
	 * Interact with user before start the round.
	 * 
	 * @param context
	 *            of the game
	 * @param round
	 *            chosen
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void waitForStart(ApplicationContext context, Round round)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		int timer = 1;
		Bomb bomb = null;
		while (round.canPlantBomb()) {
			context.renderFrame((g, contentLost) -> {
				Graphics.drawBackground(g);
				Graphics.update(g, round);
			});
			try {
				MotionEvent event;
				for (;;) {
					event = context.waitAndBlockUntilAMotion();
					if (Graphics.click(context, event,
							Graphics.LEFT_PIXEL.get(),
							Graphics.TOP_PIXEL.get(), Graphics.WIDTH.get(),
							Graphics.HEIGHT.get())) {
						break;
					}
				}
				if (bomb != null) {
					// Check to change timer
					if (Graphics.clickOnGameElement(context, event, bomb)) {
						timer = increaseTimer(bomb, timer);
					} else {
						bomb = plantBomb(round, event);
						timer = resetTimer(bomb);
					}
				} else {
					bomb = plantBomb(round, event);
					timer = resetTimer(bomb);
				}
				bomb.setTimer(timer);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		context.renderFrame((g, contentLost) -> {
			Graphics.drawBackground(g);
			Graphics.writeTextCentered(g, "Click to start the game");
		});
		for (;;) {
			try {
				if (Action.UP.equals(context.waitAndBlockUntilAMotion()
						.getAction())) {
					break;
				}
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		}
		context.renderFrame((g, contentLost) -> {
			Graphics.drawBackground(g);
		});
	}

	/**
	 * Start the game.
	 */
	public void runApplication() {
		Application
				.run(Color.BLACK,
						context -> {
							Graphics.init(context);

							Round roundTmp;
							try {
								roundTmp = getRound(context);
							} catch (IllegalStateException e) {
								Graphics.addException(e);
								try {
									Thread.sleep(1000);
								} catch (Exception e1) {
								}
								context.exit(1);
								return;
							}
							final Round round = roundTmp;

							try {
								waitForStart(context, round);
							} catch (Exception e) {
								Graphics.addException(e);
								context.renderFrame((g, contentLost) -> {
									Graphics.drawBackground(g);
								});
								try {
									Thread.sleep(3000);
								} catch (Exception e1) {
								}
								context.exit(2);
								return;
							}

							Thread thread = new Thread(() -> {
								round.start();
							});
							thread.setUncaughtExceptionHandler((t, e) -> t
									.setName(e.getLocalizedMessage()));
							thread.setName("moteur 3D");
							context.renderFrame((g, contentLost) -> {
								Graphics.drawBackground(g);
							});

							thread.start();

							long previous = System.currentTimeMillis();
							while (!round.isVictory() && !round.isDefeat()) {
								if (!thread.isAlive()) {
									Graphics.addException(new Exception(thread
											.getName()));
									context.renderFrame((g, contentLost) -> {
										Graphics.drawBackground(g);
									});
									try {
										Thread.sleep(3000);
									} catch (Exception e) {
									}
									context.exit(3);
									return;
								}
								if (System.currentTimeMillis() - previous > Graphics.REFRESH_TIME) {
									context.renderFrame((g, contentLost) -> {
										Graphics.drawBackground(g);
										Graphics.update(g, round);
									});
									previous = System.currentTimeMillis();
									try {
										Thread.sleep(Graphics.REFRESH_TIME);
									} catch (Exception e) {
										Graphics.addException(e);
									}
								}
							}

							EndRound(round, context);

							System.out.println("end.");
							try {
								Thread.sleep(3000);
							} catch (Exception e) {
								Graphics.addException(e);
							}
							context.exit(0);
						});
	}

	/**
	 * End round
	 * 
	 * @param round
	 *            to end
	 * @param context
	 *            of the game
	 */
	private void EndRound(Round round, ApplicationContext context) {
		context.renderFrame((g, contentLost) -> {
			Graphics.drawBackground(g);
			Graphics.update(g, round);
			g.setFont(new Font("Helvetica", Font.CENTER_BASELINE, 20));
			if (round.isVictory()) {
				g.setColor(Color.BLUE);
				Graphics.writeTextCentered(g, "Victory !");
			} else if (round.isDefeat()) {
				g.setColor(Color.RED);
				Graphics.writeTextCentered(g, "Defeat !");
			}
		});
	}
}
