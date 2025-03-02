package opi.botc.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.boss.ServerBossBar;

import java.util.Timer;
import java.util.TimerTask;

public class BossBarTimer {
    private ServerBossBar bossBar;
    private int timeRemaining; // Time in seconds
    private Timer timer;
    private final int totalTicks;

    public BossBarTimer(int minutes) {
        this.timeRemaining = minutes * 60;
        this.totalTicks = timeRemaining * 20;

        this.bossBar = new ServerBossBar(
                Text.literal("Time Remaining: " + minutes + ":00").formatted(Formatting.RED),
                ServerBossBar.Color.RED,
                ServerBossBar.Style.PROGRESS
        );
        this.bossBar.setPercent(1.0f); 
    }

    public void addPlayer(ServerPlayerEntity player) {
        bossBar.addPlayer(player);
    }

    public void start() {
        bossBar.setVisible(true);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int ticksRemaining = totalTicks;

            @Override
            public void run() {
                if (ticksRemaining <= 0) {
                    bossBar.setVisible(false);
                    this.cancel();

                    return;
                }

                float progress = (float) ticksRemaining / totalTicks;
                bossBar.setPercent(progress);

                int secondsRemaining = ticksRemaining / 20; 
                int displayMinutes = secondsRemaining / 60;
                int displaySeconds = secondsRemaining % 60;

                bossBar.setName(Text.literal(
                        String.format("Time Remaining: %02d:%02d", displayMinutes, displaySeconds)
                ).formatted(Formatting.RED));

                ticksRemaining--; 
            }
        }, 0, 50);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
        bossBar.clearPlayers();
    }
}
