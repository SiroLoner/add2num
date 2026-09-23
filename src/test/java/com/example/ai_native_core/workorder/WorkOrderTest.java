package com.example.ai_native_core.workorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class WorkOrderTest {

    @Test
    void creates_valid_work_order_with_default_status() {
        String id = UUID.randomUUID().toString();
        WorkOrder workOrder = WorkOrder.create(
                id,
                "EQ-1001",
                "Replace pump seal",
                WorkOrderPriority.HIGH,
                "tech-01");

        assertNotNull(workOrder);
        assertEquals(id, workOrder.id());
        assertEquals("EQ-1001", workOrder.equipmentId());
        assertEquals("Replace pump seal", workOrder.description());
        assertEquals(WorkOrderPriority.HIGH, workOrder.priority());
        assertEquals(WorkOrderStatus.NEW, workOrder.status());
        assertNotNull(workOrder.createdAt());
        assertEquals("tech-01", workOrder.createdBy());
    }

    @Test
    void rejects_blank_description() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> WorkOrder.create(
                        UUID.randomUUID().toString(),
                        "EQ-1001",
                        "   ",
                        WorkOrderPriority.MEDIUM,
                        "tech-01"));

        assertEquals("description must not be blank", exception.getMessage());
    }

    @Test
    void rejects_missing_equipment_id() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> WorkOrder.create(
                        UUID.randomUUID().toString(),
                        " ",
                        "Inspect panel",
                        WorkOrderPriority.LOW,
                        "tech-01"));

        assertEquals("equipmentId must not be blank", exception.getMessage());
    }

    @Test
    void rejects_invalid_priority() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> WorkOrder.create(
                        UUID.randomUUID().toString(),
                        "EQ-1001",
                        "Inspect panel",
                        null,
                        "tech-01"));

        assertEquals("priority is required", exception.getMessage());
    }
}
