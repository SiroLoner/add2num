# Domain Model

Tài liệu này mô tả các entity, value object và quy tắc nghiệp vụ của Core Service.

## 1. Domain overview
Domain hiện tại được giới hạn cho scenario Work Order management trong môi trường field service.

### Entity: WorkOrder
- `id`: unique identifier, bắt buộc
- `equipmentId`: mã thiết bị, bắt buộc
- `description`: mô tả công việc, bắt buộc và không được rỗng
- `priority`: enum `LOW | MEDIUM | HIGH | URGENT`
- `status`: enum `NEW | IN_PROGRESS | DONE | CANCELLED`
- `createdAt`: thời điểm tạo, auto-generated
- `createdBy`: người tạo, bắt buộc

## 2. Domain rules
- `equipmentId` không được rỗng
- `description` không được null hoặc blank
- `priority` phải được cung cấp
- `createdBy` không được rỗng
- Mặc định status khi tạo mới là `NEW`
- `createdAt` được gán tại thời điểm tạo

## 3. Value objects
- `WorkOrderPriority` enum
- `WorkOrderStatus` enum

## 4. Current implementation status
Domain model này đang ở mức scaffold và phục vụ lab/validation. Khi feature chính thức được triển khai, nên bổ sung repository layer, validation layer, authorization checks, và API contract rõ ràng.
