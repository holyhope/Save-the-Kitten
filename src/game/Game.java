package game;

import java.awt.Color;
import java.awt.Font;

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
	 */
	public Round getRound(ApplicationContext context) {
		World world = new World(new Vec2(0, 0));
		Round round = Round.create(world, 15f, 15f);
		round.add(Launcher
				.create(world, new Vec2(5, 5), 1, new Vec2(-0.001f, -0.001f)));
		round.add(Goal.create(world, new Vec2(3, 3)));
		/*round.add(Goal.create(world, new Vec2(3, 2)));
		round.add(Goal.create(world, new Vec2(2, 3)));*/
		return round;
	}

	/**
	 * Interact with user before start the round.
	 * 
	 * @param context
	 *            of the game
	 */
	public void waitForStart(ApplicationContext context) {
		KeyboardEvent key;
		context.render(g -> {
			g.setColor(Color.BLACK);
			g.setBackground(Graphics.BACKGROUND_COLOR);
			g.clearRect(0, 0, Graphics.WIDTH, Graphics.HEIGHT);
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
				.run("Cat launcher",
						Graphics.WIDTH+1,
						Graphics.HEIGHT+1,
						context -> {
							Round round = getRound(context);

							waitForStart(context);

							new Thread(() -> {
								round.start();
							}).start();

							long previous = System.currentTimeMillis();
							while (!round.isVictory() && !round.isDefeat()) {
								if (System.currentTimeMillis() - previous > Graphics.REFRESH_TIME) {
									context.render(g -> {
										Graphics.update(g, round);
									});
									previous = System.currentTimeMillis();
								} else {
									try {
										Thread.sleep(5);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							System.out.println("end.");
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
