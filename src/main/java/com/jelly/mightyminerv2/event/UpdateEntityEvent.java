package com.jelly.mightyminerv2.event;

import net.minecraft.entity.LivingEntity;

public class UpdateEntityEvent extends MMEvent {
    public final LivingEntity entity;
    public final byte type; // 0 = update, 1 = remove
    public final long packedPos;
    public UpdateEntityEvent(LivingEntity e)               { this(e, (byte)0); }
    public UpdateEntityEvent(LivingEntity e, byte type)    { this(e, type, 0L); }
    public UpdateEntityEvent(LivingEntity e, long pos)     { this(e, (byte)0, pos); }
    public UpdateEntityEvent(LivingEntity e, byte t, long p){ entity=e; type=t; packedPos=p; }
}
