using EzBuildServicesMessaging.Warehouse;

namespace EzBuildServices.Interfaces
{
    public interface IWarehouseService
    {
        GetWarehouseResponse GetWarehouse(int firmID);
        CreateUpdateWarehouseResponse CreateUpdateWarehouse(CreateUpdateWarehouseRequest request);
        GetEquipmentForWarehouseResponse GetEquipmentForWarehouse(int warehouseID);
        GetEquipmentHistoryForWarehouseResponse GetEquipmentHistoryForWarehouse(int warehouseID);
        CreateEquipmentResponse CreateEquipmentForWarehouse(CreateEquipmentRequest request);
        CreateEquipmentHistoryResponse CreateEquipmentHistoryForWarehouse(CreateEquipmentHistoryRequest request);
        DeleteWarehouseResponse DeleteWarehouse(int firmID);
        UpdateEquipmentResponse UpdateEquipment(UpdateEquipmentRequest request);
        DeleteEquipmentResponse DeleteEquipment(int equipmentID);
        GetEquipmentHistoryWarehouseResponse GetEquipmentHistoryForWarehouseResponse(int warehouseID);
        GetEquipmentHistoryForEmployeeAndDateResponse GetEquipmentHistoryForEmployeeAndDate(GetEquipmentHistoryForEmployeeAndDateRequest request);
        DeleteEquipmentHistoryResponse DeleteEquipmentHistory(DeleteEquipmentHistoryRequest request);
        UpdateEquipmentHistoryResponse UpdateEquipmentHistory(UpdateEquipmentHistoryRequest request);
    }
}
