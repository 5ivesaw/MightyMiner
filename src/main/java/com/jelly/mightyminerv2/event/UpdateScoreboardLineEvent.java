package com.jelly.mightyminerv2.event;

public class UpdateScoreboardLineEvent extends MMEvent {
    private final String line;
    public UpdateScoreboardLineEvent(String line) { this.line = line; }
    public String getLine() { return line; }
}
