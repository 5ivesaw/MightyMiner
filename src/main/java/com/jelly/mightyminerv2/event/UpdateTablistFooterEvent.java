package com.jelly.mightyminerv2.event;

import java.util.List;

public class UpdateTablistFooterEvent extends MMEvent {
    public final List<String> footer;
    public UpdateTablistFooterEvent(List<String> f) { footer=f; }
}
