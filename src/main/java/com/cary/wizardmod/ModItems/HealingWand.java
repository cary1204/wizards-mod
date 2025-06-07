package com.cary.wizardmod.ModItems;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class HealingWand extends Item {
    private static final int CIRCLE_DURATION = 50;
    private static final int COOLDOWN_DURATION = 600;
    private static final int REGEN_DURATION = 20;
    private static final double CIRCLE_RADIUS = 3.0;
    private static final int PARTICLE_COUNT = 70;

    public HealingWand(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(itemStack)) {
            return ActionResult.FAIL;
        }

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            Vec3d playerPos = user.getPos();

            createHealingCircle(serverWorld, playerPos, user);

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0F, 1.0F);

            user.getItemCooldownManager().set(itemStack, COOLDOWN_DURATION);
        }

        return ActionResult.SUCCESS;
    }

    private void createHealingCircle(ServerWorld world, Vec3d center, PlayerEntity caster) {
        new Thread(() -> {
            for (int tick = 0; tick < CIRCLE_DURATION; tick++) {
                final int currentTick = tick;

                world.getServer().execute(() -> {
                    spawnCircleParticles(world, center, currentTick);

                    applyHealingToNearbyPlayers(world, center);
                });

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void spawnCircleParticles(ServerWorld world, Vec3d center, int tick) {
        int particlesPerTick = PARTICLE_COUNT / 20;
        double angleStep = (2 * Math.PI) / particlesPerTick;
        double timeOffset = tick * 0.1;
        for (int i = 0; i < particlesPerTick; i++) {
            double angle = (i * angleStep) + timeOffset;
            double x = center.x + Math.cos(angle) * CIRCLE_RADIUS;
            double z = center.z + Math.sin(angle) * CIRCLE_RADIUS;
            double y = center.y + 0.1;

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    1, 0.1, 0.1, 0.1, 0.02);

            if (Math.random() < 0.3) {
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        x, y + Math.random() * 0.5, z,
                        1, 0.05, 0.05, 0.05, 0.01);
            }
        }

        if (tick % 5 == 0) {
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    center.x, center.y + 0.5, center.z,
                    3, 0.2, 0.2, 0.2, 0.05);
        }
    }

    private void applyHealingToNearbyPlayers(ServerWorld world, Vec3d center) {
        Box healingArea = new Box(
                center.x - CIRCLE_RADIUS, center.y - 1, center.z - CIRCLE_RADIUS,
                center.x + CIRCLE_RADIUS, center.y + 3, center.z + CIRCLE_RADIUS
        );

        List<PlayerEntity> playersInRange = world.getEntitiesByClass(
                PlayerEntity.class, healingArea,
                player -> player.squaredDistanceTo(center) <= CIRCLE_RADIUS * CIRCLE_RADIUS
        );

        for (PlayerEntity player : playersInRange) {
            StatusEffectInstance currentRegen = player.getStatusEffect(StatusEffects.REGENERATION);
            if (currentRegen == null || currentRegen.getAmplifier() < 1 || currentRegen.getDuration() < REGEN_DURATION) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION,
                        REGEN_DURATION,
                        9,
                        false,
                        true,
                        true
                ));
            }
        }
    }
}