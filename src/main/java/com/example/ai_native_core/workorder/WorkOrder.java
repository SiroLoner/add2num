package com.example.ai_native_core.workorder;

import java.time.Instant;

public record WorkOrder(
        String id,
        String equipmentId,
        String description,
        WorkOrderPriority priority,
        WorkOrderStatus status,
        Instant createdAt,
        String createdBy) {

    public static WorkOrder create(
            String id,
            String equipmentId,
            String description,
            WorkOrderPriority priority,
            String createdBy) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (equipmentId == null || equipmentId.isBlank()) {
            throw new IllegalArgumentException("equipmentId must not be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority is required");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy must not be blank");
        }

        return new WorkOrder(
                id,
                equipmentId.trim(),
                description.trim(),
                priority,
                WorkOrderStatus.NEW,
                Instant.now(),
                createdBy.trim());
    }
}
