// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.feature.impl.AutoGetStats.tasks;

public enum TaskStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILURE;

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    public boolean isFailure() {
        return this == FAILURE;
    }
}
