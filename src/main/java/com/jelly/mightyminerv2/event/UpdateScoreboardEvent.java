package com.jelly.mightyminerv2.event;

import java.util.List;

public class UpdateScoreboardEvent extends MMEvent {
    public final List<String> scoreboard;
    public final long timestamp;
    public UpdateScoreboardEvent(List<String> sb, long ts) { scoreboard=sb; timestamp=ts; }
}
