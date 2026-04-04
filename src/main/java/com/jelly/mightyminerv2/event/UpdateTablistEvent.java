package com.jelly.mightyminerv2.event;

import java.util.List;

public class UpdateTablistEvent extends MMEvent {
    public final List<String> tablist;
    public final long timestamp;
    public UpdateTablistEvent(List<String> tl, long ts) { tablist=tl; timestamp=ts; }
}
