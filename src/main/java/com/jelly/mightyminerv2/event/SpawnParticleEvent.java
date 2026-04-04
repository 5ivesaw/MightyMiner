package com.jelly.mightyminerv2.event;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

public class SpawnParticleEvent extends MMEvent {
    private final ParticleEffect particleType;
    private final double x, y, z, ox, oy, oz;
    private final float speed;

    public SpawnParticleEvent(ParticleEffect type, double x, double y, double z,
                               double ox, double oy, double oz, float speed) {
        this.particleType = type; this.x=x; this.y=y; this.z=z;
        this.ox=ox; this.oy=oy; this.oz=oz; this.speed=speed;
    }
    public ParticleEffect getParticleType() { return particleType; }
    public Vec3d getPos() { return new Vec3d(x, y, z); }
}
