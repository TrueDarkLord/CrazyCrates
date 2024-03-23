package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ParticleAnimation;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Particles extends CrateBuilder {

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final BukkitUserManager userManager = this.plugin.getUserManager();

    public Particles(Crate crate, Player player, int size, Location location) {
        super(crate, player, size, location);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        this.crateManager.addCrateInUse(getPlayer(), getLocation());

        boolean keyCheck = this.userManager.takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            MiscUtils.failedToTakeKey(getPlayer(), getCrate().getName());
            this.crateManager.removePlayerFromOpeningList(getPlayer());
            return;
        }

        if (this.crateManager.getHolograms() != null) {
            this.crateManager.getHolograms().removeHologram(getLocation().getBlock());
        }

        addCrateTask(new BukkitRunnable() {
            int tickTillPrize = 0;

            @Override
            public void run() {

                for (ParticleAnimation PA : getCrate().getParticleAnimations()) {

                    String animation = PA.getAnimation().toLowerCase().contains(":") ? PA.getAnimation().toLowerCase().split(":")[0] : PA.getAnimation().toLowerCase();

                    switch (animation) {
                        case "spiral" ->
                                spiral(PA, getLocation(), tickTillPrize);
                        case "helix" ->
                                helix(PA, getLocation(), tickTillPrize);
                        case "circle" ->
                                circle(PA, getLocation(), tickTillPrize, PA.getAnimation());
                        case "tf" ->
                                twirlyBullshit(PA, getLocation(), tickTillPrize, PA.getAnimation());
                        default ->
                                lineUp(PA, getLocation(), tickTillPrize);
                    }
                }

                if (++tickTillPrize >= 60) {
                    crateManager.endCrate(getPlayer());
                    QuickCrate quickCrate = new QuickCrate(getCrate(), getPlayer(), getLocation());
                    quickCrate.open(KeyType.free_key, false);
                }
            }
        }.runTaskTimer(this.plugin, 0, 1));
    }

    private static void spawnParticles(Particle particle, Color color, Location location) {
        if (particle == Particle.REDSTONE) {
            location.getWorld().spawnParticle(particle, location, 0, new Particle.DustOptions(color, 1));
        } else {
            location.getWorld().spawnParticle(particle, location, 0);
        }
    }

    private static void twirlyBullshit(ParticleAnimation PA, Location loc, int tickTillPrize, String PAAnimation) {
        double y;
        double off = 0; //YOffSet
        try {
            y = Double.parseDouble(PAAnimation.split(":")[1]);
        } catch (Exception e) {
            y = 0.0;
        }

        for (int i=0; i < 20; i++) {
            circle(PA, loc.add(0, -off, 0), tickTillPrize, PAAnimation);
            off -= 0.1;
        }
    }

    private static void circle(ParticleAnimation PA, Location loc, int tickTillPrize, String PAAnimation, Double height) {

        Particle particle = Particle.valueOf(PA.getParticle());
        Color color = Color.fromRGB(PA.getColor());

        double angle = tickTillPrize * 6.0;

        double x = Math.cos(angle);
        double z = Math.sin(angle);
        double y;

        try {
            y = Double.parseDouble(PAAnimation.split(":")[1]);
        } catch (Exception e) {
            y = 0;
        }

        Location location = loc.clone().add(.5, 0, .5).add(x, y, z);

        spawnParticles(particle, color, location);

    }
    private static void circle(ParticleAnimation PA, Location loc, int tickTillPrize, String PAAnimation) {

        Particle particle = Particle.valueOf(PA.getParticle());
        Color color = Color.fromRGB(PA.getColor());

        double angle = tickTillPrize * 6.0;

        double x = Math.cos(angle);
        double z = Math.sin(angle);
        double y;

        try {
            y = Double.parseDouble(PAAnimation.split(":")[1]);
        } catch (Exception e) {
            y = 0;
        }

        Location location = loc.clone().add(.5, 0, .5).add(x, y, z);

        spawnParticles(particle, color, location);

    }
    private static void helix(ParticleAnimation PA ,Location loc, int tickTillPrize) {

        String Properties = PA.getAnimation().split(":").length < 2 ? "" : PA.getAnimation().toLowerCase().split(":")[1];
        Particle particle = Particle.valueOf(PA.getParticle());
        Color color = Color.fromRGB(PA.getColor());
        double angle = tickTillPrize * 6.0;
        double amplitude = Properties.contains("amplitude=") ? Double.parseDouble(Properties.split("amplitude=")[1]) : 1;
        double y = !Properties.contains("down") ? tickTillPrize / 15.0 : 4 - (tickTillPrize / 15.0);

        if (Properties.contains("diverge")) amplitude = (tickTillPrize / 60.0) * amplitude;

        double x, z;
        if (!Properties.contains("left")) {
            x = amplitude *  Math.sin(angle);
            z = amplitude *  Math.cos(angle);
        } else {
            z = amplitude *  Math.sin(angle);
            x = amplitude *  Math.cos(angle);
        }

        Location location = loc.clone().add(.5, 0, .5).add(x, y, z);

        spawnParticles(particle, color, location);
    }
    private static void spiral(ParticleAnimation PA, Location loc, int tickTillPrize) {

        Particle particle = Particle.valueOf(PA.getParticle());
        Color color = Color.fromRGB(PA.getColor());
        boolean clockwise = PA.getAnimation().contains("clockwise");

        Location particleLocation = loc.clone().add(.5, 3, .5);

        spawnParticles(particle, color, spiralLocations(particleLocation, clockwise).get(tickTillPrize));

    }
    private static void lineUp(ParticleAnimation PA, Location loc, int tickTillPrize) {

        Particle particle = Particle.valueOf(PA.getParticle());
        Color color = Color.fromRGB(PA.getColor());

        Location particleLocation = loc.clone().add(.5, 3 - 3/(tickTillPrize * 1.0), .5);
        spawnParticles(particle, color, particleLocation);

    }

    private static ArrayList<Location> spiralLocations(Location center, boolean clockWise) {
        World world = center.getWorld();

        double downWardsDistance = .05;
        double expandingRadius = .08;

        double centerY = center.getY();
        double radius = 0;

        int particleAmount = 10;
        int radiusIncrease = 0;

        int nextLocation = 0;

        double increment = (2*Math.PI) / particleAmount;

        ArrayList<Location> locations = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            double angle = nextLocation * increment;

            double x;
            double z;

            if (clockWise) {
                x = center.getX() + (radius * Math.cos(angle));
                z = center.getZ() + (radius * Math.sin(angle));
            } else {
                z = center.getZ() - (radius * Math.cos(angle));
                x = center.getX() - (radius * Math.sin(angle));
            }

            locations.add(new Location(world, x, centerY, z));
            centerY -= downWardsDistance;
            nextLocation++;
            radiusIncrease++;

            if (radiusIncrease == 6) {
                radiusIncrease = 0;
                radius += expandingRadius;
            }

            if (nextLocation == 10) nextLocation = 0;
        }

        return locations;
    }

    @Override
    public void run() {

    }
}