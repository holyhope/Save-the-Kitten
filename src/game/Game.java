package game;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.umlv.zen4.Application;
import fr.umlv.zen4.ApplicationContext;
import fr.umlv.zen4.MotionEvent;
import fr.umlv.zen4.MotionEvent.Action;

public class Game {

	/**
	 * Get a round.
	 * 
	 * @param context
	 *            of the current game
	 * @return new Round
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Round getRound() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 5f, 5f);
		Launcher launcher = Launcher.create(world, new Vec2(3, 2), 1, new Vec2(
				0.001f, -0.001f));
		launcher.addBullet(Cat.class);
		round.add(launcher);
		round.add(Goal.create(world, new Vec2(3, 3)));
		round.add(Goal.create(world, new Vec2(2, 3)));
		return round;
	}

	/**
	 * Interact with user before start the round.
	 * 
	 * @param context
	 *            of the game
	 * @param round
	 *            chosen
	 */
	public void waitForStart(ApplicationContext context, Round round) {
		MotionEvent event;
		context.renderFrame((g, contentLost) -> {
			Graphics.drawBackground(g);
			Graphics.writeTextCentered(g, "Click to start the game");
		});
		for (;;) {
			try {
				event = context.waitAndBlockUntilAMotion();
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
			if (event.getAction().equals(Action.UP)) {
				break;
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
							Round roundTmp = null;
							do {
								try {
									roundTmp = getRound();
								} catch (Throwable e) {
									Graphics.addException(context, e);
								}
							} while (roundTmp == null);

							final Round round = roundTmp;

							waitForStart(context, round);

							new Thread(() -> {
								round.start();
							}).start();

							long previous = System.currentTimeMillis();
							while (!round.isVictory() && !round.isDefeat()) {
								if (System.currentTimeMillis() - previous > Graphics.REFRESH_TIME) {
									context.renderFrame((g, contentLost) -> {
										Graphics.drawBackground(g);
										Graphics.update(g, round);
									});
									previous = System.currentTimeMillis();
								} else {
									try {
										Thread.sleep(1);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							EndRound(round, context);

							System.out.println("end.");
							try {
								Thread.sleep(3000);
							} catch (Exception e) {
								Graphics.addException(context, e);
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
			g.dispose();
		});
	}
}
