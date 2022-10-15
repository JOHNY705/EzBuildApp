using EzBuildDataAccess;
using EzBuildDataAccess.Repository.Factory;
using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.ViewModels;
using EzBuildServicesMessaging.Warehouse;
using System;
using System.Collections.Generic;

namespace EzBuildServices.Implementations
{
    class WarehouseService : IWarehouseService
    {
        private readonly IWarehouseRepository warehouseRepository = RepositoryFactory.GetWarehouseRepository();

        public GetWarehouseResponse GetWarehouse(int firmID)
        {
            GetWarehouseResponse response = new GetWarehouseResponse();

            try
            {
                response.Warehouse = warehouseRepository.GetWarehouse(firmID);
                if (response.Warehouse.IDWarehouse == 0)
                {
                    response.IsSuccessful = false;
                    response.Message = "No Warehouse found for FirmID";
                }
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public CreateUpdateWarehouseResponse CreateUpdateWarehouse(CreateUpdateWarehouseRequest request)
        {
            CreateUpdateWarehouseResponse response = new CreateUpdateWarehouseResponse();

            try
            {
                warehouseRepository.CreateUpdateWarehouse(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEquipmentForWarehouseResponse GetEquipmentForWarehouse(int warehouseID)
        {
            GetEquipmentForWarehouseResponse response = new GetEquipmentForWarehouseResponse();

            try
            {
                IEnumerable<Equipment> equipment = warehouseRepository.GetEquipmentForWarehouse(warehouseID);

                ICollection<EquipmentVM> equipmentVMList = new List<EquipmentVM>();

                foreach (Equipment e in equipment)
                {
                    equipmentVMList.Add(new EquipmentVM
                    {
                        IDEquipment = e.IDEquipment,
                        Base64Image = e.Base64Image,
                        EquipmentName = e.EquipmentName,
                        Quantity = e.Quantity,
                        EquipmentDescription = e.EquipmentDescription
                    });
                }

                response.Equipment = equipmentVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEquipmentHistoryForWarehouseResponse GetEquipmentHistoryForWarehouse(int warehouseID)
        {
            GetEquipmentHistoryForWarehouseResponse response = new GetEquipmentHistoryForWarehouseResponse();

            try
            {
                response.EquipmentHistory = warehouseRepository.GetEquipmentHistoryForWarehouse(warehouseID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;

        }

        public CreateEquipmentResponse CreateEquipmentForWarehouse(CreateEquipmentRequest request)
        {
            CreateEquipmentResponse response = new CreateEquipmentResponse();

            try
            {
                warehouseRepository.CreateEquipmentForWarehouse(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public CreateEquipmentHistoryResponse CreateEquipmentHistoryForWarehouse(CreateEquipmentHistoryRequest request)
        {
            CreateEquipmentHistoryResponse response = new CreateEquipmentHistoryResponse();

            try
            {
                warehouseRepository.CreateEquipmentHistoryForWarehouse(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteWarehouseResponse DeleteWarehouse(int firmID)
        {
            DeleteWarehouseResponse response = new DeleteWarehouseResponse();

            try
            {
                warehouseRepository.DeleteWarehouse(firmID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateEquipmentResponse UpdateEquipment(UpdateEquipmentRequest request)
        {
            UpdateEquipmentResponse response = new UpdateEquipmentResponse();

            try
            {
                response = warehouseRepository.UpdateEquipment(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteEquipmentResponse DeleteEquipment(int equipmentID)
        {
            DeleteEquipmentResponse response = new DeleteEquipmentResponse();

            try
            {
                warehouseRepository.DeleteEquipment(equipmentID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEquipmentHistoryWarehouseResponse GetEquipmentHistoryForWarehouseResponse(int warehouseID)
        {
            GetEquipmentHistoryWarehouseResponse response = new GetEquipmentHistoryWarehouseResponse();

            try
            {
                response.EquipmentHistory = warehouseRepository.GetEquipmentHistoryWarehouse(warehouseID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEquipmentHistoryForEmployeeAndDateResponse GetEquipmentHistoryForEmployeeAndDate(GetEquipmentHistoryForEmployeeAndDateRequest request)
        {
            GetEquipmentHistoryForEmployeeAndDateResponse response = new GetEquipmentHistoryForEmployeeAndDateResponse();

            try
            {
                response.EquipmentHistory = warehouseRepository.GetEquipmentHistoryForEmployeeAndDate(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteEquipmentHistoryResponse DeleteEquipmentHistory(DeleteEquipmentHistoryRequest request)
        {
            DeleteEquipmentHistoryResponse response = new DeleteEquipmentHistoryResponse();

            try
            {
                warehouseRepository.DeleteEquipmentHistory(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateEquipmentHistoryResponse UpdateEquipmentHistory(UpdateEquipmentHistoryRequest request)
        {
            UpdateEquipmentHistoryResponse response = new UpdateEquipmentHistoryResponse();

            try
            {
                warehouseRepository.UpdateEquipmentHistory(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }
    }
}
