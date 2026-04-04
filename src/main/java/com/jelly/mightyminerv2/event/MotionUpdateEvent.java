package com.jelly.mightyminerv2.event;

public class MotionUpdateEvent extends MMEvent implements EventBus.Cancellable {
    public float yaw;
    public float pitch;
    private boolean canceled;
    public MotionUpdateEvent(float yaw, float pitch) { this.yaw = yaw; this.pitch = pitch; }
    @Override public boolean isCanceled()          { return canceled; }
    @Override public void setCanceled(boolean v)   { canceled = v; }
}
