package com.cary.wizardmod.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CustomFireballEntity extends FireballEntity {
    private static final float IMPACT_DAMAGE = 25.0f; //damge of fireball
    private final int customExplosionPower;

    public CustomFireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super(EntityType.FIREBALL, world);
        this.setOwner(owner);
        this.setVelocity(velocity);
        this.customExplosionPower = explosionPower;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.getWorld().isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) hitResult;
                Entity hitEntity = entityHit.getEntity();

                // collide dam
                if (hitEntity instanceof LivingEntity && this.getOwner() != null) {
                    DamageSource damageSource = this.getWorld().getDamageSources().create(
                            DamageTypes.FIREBALL, this, this.getOwner()
                    );
                    hitEntity.damage((ServerWorld) this.getWorld(), damageSource, IMPACT_DAMAGE);
                }
            }

            this.getWorld().createExplosion(
                    this,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    this.customExplosionPower,
                    true,
                    World.ExplosionSourceType.MOB
            );

            this.discard();
        }
    }
}