package com.jelly.mightyminerv2.event;

import net.minecraft.network.packet.Packet;

public abstract class PacketEvent extends MMEvent {
    public final Packet<?> packet;
    protected PacketEvent(Packet<?> p) { packet = p; }
    public static class Received extends PacketEvent { public Received(Packet<?> p){super(p);} }
    public static class Sent    extends PacketEvent { public Sent(Packet<?> p){super(p);} }
}
