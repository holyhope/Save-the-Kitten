package game;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.umlv.zen3.Application;
import fr.umlv.zen3.ApplicationContext;
import fr.umlv.zen3.KeyboardEvent;
import fr.umlv.zen3.KeyboardKey;

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
	public Round getRound(ApplicationContext context)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 5f, 5f);
		Launcher launcher = Launcher.create(world, new Vec2(3, 2), 1, new Vec2(
				-0.001f, -0.001f));
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
	 */
	public void waitForStart(ApplicationContext context, Round round) {
		KeyboardEvent key;
		context.render(g -> {
			g.setColor(Color.BLACK);
			g.setBackground(Graphics.BACKGROUND_COLOR);
			g.clearRect(0, 0, Graphics.WIDTH, Graphics.HEIGHT);
			Graphics.drawGrid(g, 10);
			round.draw(g);
			Graphics.writeTextCentered(g, "Press Space to start the game");
		});
		while (true) {
			key = context.waitKeys();
			if (KeyboardKey.SPACE.equals(key.getKey())) {
				break;
			}
		}
		context.render(g -> {
			g.setBackground(Graphics.BACKGROUND_COLOR);
			g.clearRect(0, 0, Graphics.WIDTH, Graphics.HEIGHT);
		});
	}

	/**
	 * Start the game.
	 */
	public void runApplication() {
		Application
				.run("Save the kitten",
						Graphics.WIDTH + 1,
						Graphics.HEIGHT + 1,
						context -> {
							Round roundTmp = null;
							do {
								try {
									roundTmp = getRound(context);
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
									context.render(g -> {
										g.setBackground(Graphics.BACKGROUND_COLOR);
										Graphics.update(g, round);
									});
									previous = System.currentTimeMillis();
									try {
										Thread.sleep(Graphics.REFRESH_TIME);
									} catch (Exception e) {
										Graphics.addException(
												context,
												new IllegalMonitorStateException(
														"Impossible de mettre en pause le processus d'affichage"));
									}
								} else {
									try {
										Thread.sleep(1);
									} catch (Exception e) {
										Graphics.addException(
												context,
												new IllegalMonitorStateException(
														"Impossible de mettre en pause le processus d'affichage"));
									}
								}
							}

							EndRound(round, context);
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
		context.render(g -> {
			g.setBackground(Graphics.BACKGROUND_COLOR);
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
		System.out.println("end.");
	}
}
