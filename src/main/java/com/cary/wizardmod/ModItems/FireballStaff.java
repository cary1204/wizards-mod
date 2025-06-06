package com.cary.wizardmod.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireballStaff extends Item {
    private static final int COOLDOWN_TICKS = 20; // 1 second (20 ticks = 1 second)

    public FireballStaff(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Check if player is on cooldown
        if (user.getItemCooldownManager().isCoolingDown(itemStack)) {
            return ActionResult.FAIL;
        }

        if (!world.isClient) {
            // Get the player's look direction
            Vec3d lookDirection = user.getRotationVector();

            // Create the custom fireball entity with higher damage and some fire
            Vec3d velocity = lookDirection.multiply(2.5); // Fast speed
            CustomFireballEntity fireball = new CustomFireballEntity(world, user, velocity, 1); // Smaller explosion power for ground damage

            // Set the fireball's position slightly in front of the player
            fireball.setPosition(
                    user.getX() + lookDirection.x * 1.5,
                    user.getEyeY() + lookDirection.y * 1.5,
                    user.getZ() + lookDirection.z * 1.5
            );

            // Spawn the fireball
            world.spawnEntity(fireball);

            // Play sound effect
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        // Apply cooldown
        user.getItemCooldownManager().set(itemStack, COOLDOWN_TICKS);

        return ActionResult.SUCCESS;
    }
}