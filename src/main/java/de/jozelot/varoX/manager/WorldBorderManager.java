package de.jozelot.varoX.manager;

import de.jozelot.varoX.VaroX;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitTask;

public class WorldBorderManager {

    private final VaroX plugin;
    private final ConfigManager configManager;
    private BukkitTask borderTask;
    private final StatesManager statesManager;

    public WorldBorderManager(VaroX plugin, ConfigManager configManager, StatesManager statesManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.statesManager = statesManager;
    }

    public void startWorldBorderSystem() {
        World overworld = Bukkit.getWorld("world");
        World nether = Bukkit.getWorld("world_nether");

        if (overworld == null) {
            plugin.getLogger().severe("Error finding 'world'");
            return;
        }

        WorldBorder overworldBorder = overworld.getWorldBorder();
        WorldBorder netherBorder = (nether != null) ? nether.getWorldBorder() : null;

        double centerX = configManager.getWorldboarderCenterX();
        double centerZ = configManager.getWorldboarderCenterZ();
        int startSize = configManager.getStartSize();

        overworldBorder.setCenter(centerX, centerZ);
        overworldBorder.setSize(startSize);
        overworldBorder.setDamageBuffer(5.0);
        overworldBorder.setDamageAmount(0.2);
        overworldBorder.setWarningDistance(50);

        plugin.getLogger().info("Overworld WorldBorder initialised: Size " + startSize + "x" + startSize + " bei X=" + centerX + ", Z=" + centerZ);

        if (netherBorder != null) {
            double netherCenterX = centerX / 8.0;
            double netherCenterZ = centerZ / 8.0;
            double netherStartSize = (double) startSize / 8.0;

            netherBorder.setCenter(netherCenterX, netherCenterZ);
            netherBorder.setSize(netherStartSize);

            netherBorder.setDamageBuffer(5.0);
            netherBorder.setDamageAmount(0.2);
            netherBorder.setWarningDistance(50);

            plugin.getLogger().info(String.format("Nether WorldBorder initialised (1:8): Size %.1fx%.1f bei X=%.1f, Z=%.1f", netherStartSize, netherStartSize, netherCenterX, netherCenterZ));
        } else {
            plugin.getLogger().warning("Error finding 'world_nether'");
        }

        scheduleNextPhase(1);
    }

    private void scheduleNextPhase(int phaseNumber) {
        String phasePath = "border_phases." + phaseNumber;

        if (!plugin.getConfig().contains(phasePath)) {
            plugin.getLogger().info("Finished all phases. WorldBorder movement ended");
            statesManager.clearWorldBorderState();
            return;
        }

        long delaySeconds = plugin.getConfig().getLong(phasePath + ".delay_seconds");
        double targetSize = plugin.getConfig().getDouble(phasePath + ".target_size");
        long durationSeconds = plugin.getConfig().getLong(phasePath + ".duration_seconds");

        long delayTicks = delaySeconds * 20L;

        statesManager.saveWorldBorderDelayState(
                phaseNumber,
                delaySeconds,
                System.currentTimeMillis()
        );

        borderTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World overworld = Bukkit.getWorld("world");
            World nether = Bukkit.getWorld("world_nether");

            if (overworld == null) return;
            WorldBorder overworldBorder = overworld.getWorldBorder();

            long shrinkStartTimeMillis = System.currentTimeMillis();
            statesManager.saveWorldBorderShrinkState(
                    phaseNumber,
                    targetSize,
                    durationSeconds,
                    shrinkStartTimeMillis
            );

            overworldBorder.setSize(targetSize, durationSeconds);

            if (nether != null) {
                WorldBorder netherBorder = nether.getWorldBorder();
                double netherTargetSize = targetSize / 8.0;

                netherBorder.setSize(netherTargetSize, durationSeconds);
            }

            plugin.getLogger().info(String.format("Phase %d started: Schrinking to %.0f (Overworld) in %d seconds.", phaseNumber, targetSize, durationSeconds));

            long nextPhaseDelayTicks = durationSeconds * 20L;

            Bukkit.getScheduler().runTaskLater(plugin, () -> scheduleNextPhase(phaseNumber + 1), nextPhaseDelayTicks);

        }, delayTicks);

        plugin.getLogger().info(String.format("Phase %d is planned to start in %d seconds.", phaseNumber, delaySeconds));
    }

    public void resumeWorldBorderSystem() {
        int activePhase = statesManager.getActiveBorderPhase();

        if (activePhase <= 0) {
            plugin.getLogger().info("Kein gespeicherter WorldBorder-Zustand gefunden. Warte auf manuellen Start oder geplanten Event-Start.");
            return;
        }

        plugin.getLogger().warning("Wiederherstellung des WorldBorder-Zustands für Phase " + activePhase + "...");

        long delayStartTimeMillis = statesManager.getBorderDelayStartTimeMillis();

        if (delayStartTimeMillis > 0) {
            long plannedDelaySeconds = statesManager.getBorderDelayDurationSeconds();
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - delayStartTimeMillis;
            long remainingDelaySeconds = plannedDelaySeconds - (elapsedTimeMillis / 1000);

            if (remainingDelaySeconds > 0) {
                long remainingDelayTicks = remainingDelaySeconds * 20L;
                plugin.getLogger().info(String.format("Delay-Timer fortgesetzt. Schrumpfung (Phase %d) startet in %d Sekunden.", activePhase, remainingDelaySeconds));

                Bukkit.getScheduler().runTaskLater(plugin, () -> startShrinkProcess(activePhase), remainingDelayTicks);
            } else {
                plugin.getLogger().info("Delay-Timer war bereits abgelaufen. Starte Schrumpfung für Phase " + activePhase + " sofort.");
                startShrinkProcess(activePhase);
            }
        }

        else {
            long shrinkStartTimeMillis = statesManager.getBorderStartTimeMillis();
            long durationSeconds = statesManager.getBorderDurationSeconds();
            double targetSize = statesManager.getBorderTargetSize();

            // Berechne die verbleibende Zeit
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - shrinkStartTimeMillis;
            long remainingDurationSeconds = durationSeconds - (elapsedTimeMillis / 1000);

            World overworld = Bukkit.getWorld("world");
            World nether = Bukkit.getWorld("world_nether");

            if (remainingDurationSeconds > 0) {

                overworld.getWorldBorder().setSize(targetSize, remainingDurationSeconds);

                if (nether != null) {
                    double netherTargetSize = targetSize / 8.0;
                    nether.getWorldBorder().setSize(netherTargetSize, remainingDurationSeconds);
                }

                plugin.getLogger().warning(String.format("WorldBorder-Schrumpfung (Phase %d) fortgesetzt. Verbleibende Zeit: %d Sekunden bis zur Zielgröße %.0f (Overworld).",
                        activePhase, remainingDurationSeconds, targetSize));

                long nextPhaseDelayTicks = remainingDurationSeconds * 20L;
                Bukkit.getScheduler().runTaskLater(plugin, () -> scheduleNextPhase(activePhase + 1), nextPhaseDelayTicks);

            } else {
                plugin.getLogger().info("WorldBorder-Phase " + activePhase + " war bei Neustart bereits abgelaufen. Starte die nächste Phase: " + (activePhase + 1));
                scheduleNextPhase(activePhase + 1);
            }
        }
    }

    private void startShrinkProcess(int phaseNumber) {
        String phasePath = "border_phases." + phaseNumber;
        if (!plugin.getConfig().contains(phasePath)) return;

        double targetSize = plugin.getConfig().getDouble(phasePath + ".target_size");
        long durationSeconds = plugin.getConfig().getLong(phasePath + ".duration_seconds");

        World overworld = Bukkit.getWorld("world");
        World nether = Bukkit.getWorld("world_nether");

        if (overworld == null) return;
        WorldBorder overworldBorder = overworld.getWorldBorder();

        long shrinkStartTimeMillis = System.currentTimeMillis();
        statesManager.saveWorldBorderShrinkState(
                phaseNumber,
                targetSize,
                durationSeconds,
                shrinkStartTimeMillis
        );

        overworldBorder.setSize(targetSize, durationSeconds);
        if (nether != null) {
            WorldBorder netherBorder = nether.getWorldBorder();
            double netherTargetSize = targetSize / 8.0;
            netherBorder.setSize(netherTargetSize, durationSeconds);
        }

        long nextPhaseDelayTicks = durationSeconds * 20L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> scheduleNextPhase(phaseNumber + 1), nextPhaseDelayTicks);

        plugin.getLogger().info(String.format("Phase %d gestartet: Schrinking to %.0f (Overworld) in %d seconds.", phaseNumber, targetSize, durationSeconds));
    }

    public BukkitTask getBorderTask() {
        return borderTask;
    }
}