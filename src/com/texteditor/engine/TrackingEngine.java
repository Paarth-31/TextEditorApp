package com.texteditor.engine;

import com.texteditor.model.ChangeTracker;
import java.util.*;

/**
 * Manages change tracking and document modification tracking.
 */
public class TrackingEngine {

    private List<ChangeTracker> changes;
    private boolean trackingEnabled;

    public TrackingEngine() {
        this.changes = new ArrayList<>();
        this.trackingEnabled = false;
    }

    public void enableTracking() {
        this.trackingEnabled = true;
    }

    public void disableTracking() {
        this.trackingEnabled = false;
    }

    public boolean isTrackingEnabled() {
        return trackingEnabled;
    }

    public void recordInsertion(String content, int position) {
        if (trackingEnabled) {
            ChangeTracker change = new ChangeTracker(
                    ChangeTracker.ChangeType.INSERT,
                    content,
                    position
            );
            changes.add(change);
        }
    }

    public void recordDeletion(String content, int position) {
        if (trackingEnabled) {
            ChangeTracker change = new ChangeTracker(
                    ChangeTracker.ChangeType.DELETE,
                    content,
                    position
            );
            changes.add(change);
        }
    }

    public void recordFormatChange(String oldValue, String newValue, int position) {
        if (trackingEnabled) {
            ChangeTracker change = new ChangeTracker(
                    ChangeTracker.ChangeType.FORMAT_CHANGE,
                    "",
                    position
            );
            change.setOldValue(oldValue);
            change.setNewValue(newValue);
            changes.add(change);
        }
    }

    public List<ChangeTracker> getAllChanges() {
        return new ArrayList<>(changes);
    }

    public List<ChangeTracker> getPendingChanges() {
        List<ChangeTracker> pending = new ArrayList<>();
        for (ChangeTracker change : changes) {
            if (!change.isAccepted()) {
                pending.add(change);
            }
        }
        return pending;
    }

    public void acceptChange(ChangeTracker change) {
        change.setAccepted(true);
    }

    public void rejectChange(ChangeTracker change) {
        changes.remove(change);
    }

    public void clearAllChanges() {
        changes.clear();
    }
}
