using EzBuildServicesMessaging.ViewModels;
using EzBuildServicesMessaging.Warehouse;
using System.Collections.Generic;

namespace EzBuildDataAccess.Repository.Interfaces
{
    public interface IWarehouseRepository
    {
        WarehouseVM GetWarehouse(int firmID);
        void CreateUpdateWarehouse(CreateUpdateWarehouseRequest request);
        IEnumerable<Equipment> GetEquipmentForWarehouse(int warehouseID);
        IEnumerable<EquipmentHistoryVM> GetEquipmentHistoryForWarehouse(int warehouseID);
        void CreateEquipmentForWarehouse(CreateEquipmentRequest request);
        void CreateEquipmentHistoryForWarehouse(CreateEquipmentHistoryRequest request);
        void DeleteWarehouse(int warehouseID);
        UpdateEquipmentResponse UpdateEquipment(UpdateEquipmentRequest request);
        void DeleteEquipment(int equipmentID);
        List<EquipmentHistoryWarehouseVM> GetEquipmentHistoryWarehouse(int warehouseID);
        IEnumerable<EquipmentHistoryVM> GetEquipmentHistoryForEmployeeAndDate(GetEquipmentHistoryForEmployeeAndDateRequest request);
        void DeleteEquipmentHistory(DeleteEquipmentHistoryRequest request);
        void UpdateEquipmentHistory(UpdateEquipmentHistoryRequest request);
    }
}
