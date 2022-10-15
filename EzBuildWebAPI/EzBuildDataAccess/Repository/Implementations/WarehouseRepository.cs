using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServicesMessaging.ViewModels;
using EzBuildServicesMessaging.Warehouse;
using System;
using System.Collections.Generic;
using System.Linq;

namespace EzBuildDataAccess.Repository.Implementations
{
    class WarehouseRepository : IWarehouseRepository
    {
        private readonly EzBuildDBEntities _context = new EzBuildDBEntities();

        public WarehouseVM GetWarehouse(int firmID)
        {
            using (var context = _context)
            {
                var warehouseVM = new WarehouseVM();

                var firm = context.Firms.FirstOrDefault(f => f.IDFirm == firmID);
                if (firm != null)
                {
                    var warehouse = context.Warehouses.FirstOrDefault(w => w.IDWarehouse == firm.WarehouseID);
                    if (warehouse != null)
                    {
                        warehouseVM.IDWarehouse = warehouse.IDWarehouse;
                        warehouseVM.FullAddress = warehouse.FullAddress;
                        if (warehouse.Employee != null)
                        {
                            warehouseVM.WarehouseManager = new EmployeeVM(
                                warehouse.Employee.IDEmployee, warehouse.Employee.FirebaseID, warehouse.Employee.FullName, 
                                warehouse.Employee.Email, warehouse.Employee.Phone, warehouse.Employee.FirmID,
                                warehouse.Employee.EmployeeTypeID, warehouse.Employee.ConstructionSiteID);
                        }
                    }
                }
                
                return warehouseVM;
            }
        }

        public void CreateUpdateWarehouse(CreateUpdateWarehouseRequest request)
        {
            using (var context = _context)
            {
                var firm = context.Firms.FirstOrDefault(f => f.IDFirm == request.FirmID);

                if (firm != null)
                {
                    var warehouse = firm.Warehouse;
                    if (warehouse != null)
                    {
                        warehouse.FullAddress = request.FullAddress;
                        warehouse.WarehouseManagerID = request.WarehouseManagerID;
                        context.SaveChanges();
                    } 
                    else
                    {
                        var newWarehouse = new Warehouse
                        {
                            FullAddress = request.FullAddress,
                            WarehouseManagerID = request.WarehouseManagerID,
                        };
                        context.Warehouses.Add(newWarehouse);
                        context.SaveChanges();

                        firm.WarehouseID = newWarehouse.IDWarehouse;
                        context.SaveChanges();
                    }
                }
            }
        }

        public IEnumerable<Equipment> GetEquipmentForWarehouse(int warehouseID)
        {
            using (var context = _context)
            {
                return context.Equipments.Where(e => e.WarehouseID == warehouseID).ToList();
            }
        }

        public IEnumerable<EquipmentHistoryVM> GetEquipmentHistoryForWarehouse(int warehouseID)
        {
            using (var context = _context)
            {
                var equipmentHistoryVM = new List<EquipmentHistoryVM>();
                var equipmentHistory = context.EquipmentHistories.Where(eh => eh.WarehouseID == warehouseID);

                if (equipmentHistory != null)
                {
                    foreach (var eq in equipmentHistory)
                    {
                        var employee = eq.Employee;
                        var employeeVM = new EmployeeVM(
                            employee.IDEmployee, employee.FirebaseID,
                            employee.FullName, employee.Email, employee.Phone, employee.FirmID,
                            employee.EmployeeTypeID, employee.ConstructionSiteID
                        );

                        var equipment = eq.Equipment;
                        var equipmentVM = new EquipmentVM
                        {
                            IDEquipment = equipment.IDEquipment,
                            Base64Image = equipment.Base64Image,
                            EquipmentName = equipment.EquipmentName,
                            Quantity = equipment.Quantity,
                            EquipmentDescription = equipment.EquipmentDescription
                        };

                        equipmentHistoryVM.Add(new EquipmentHistoryVM
                        {
                            Equipment = equipmentVM, 
                            Employee = employeeVM, 
                            DateEquipmentTaken = eq.DateEquipmentTaken, 
                            QuantityTaken = eq.QuantityTaken,
                            WarehouseID = eq.WarehouseID
                        });
                    }
                }

                return equipmentHistoryVM;
            }
        }

        public void CreateEquipmentForWarehouse(CreateEquipmentRequest request)
        {
            using (var context = _context)
            {
                Equipment equipment = new Equipment
                {
                    Base64Image = request.Base64Image,
                    EquipmentDescription = request.EquipmentDescription,
                    EquipmentName = request.EquipmentName,
                    Quantity = request.Quantity,
                    WarehouseID = request.WarehouseID
                };

                context.Equipments.Add(equipment);
                context.SaveChanges();
            }
        }

        public void CreateEquipmentHistoryForWarehouse(CreateEquipmentHistoryRequest request)
        {
            using (var context = _context)
            {
                var equipmentHistory = new List<EquipmentHistory>();
                var currentDateAndTime = DateTime.Now;

                foreach (var eh in request.EquipmentHistory)
                {
                    var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == eh.EquipmentID);
                    if (equipment != null)
                    {
                        equipment.Quantity -= eh.QuantityTaken;
                    }

                    context.EquipmentHistories.Add(new EquipmentHistory
                    {
                        DateEquipmentTaken = currentDateAndTime,
                        QuantityTaken = eh.QuantityTaken,
                        EmployeeID = request.EmployeeID,
                        EquipmentID = eh.EquipmentID,
                        WarehouseID = request.WarehouseID
                    });

                }

                context.SaveChanges();
            }
        }

        public void DeleteWarehouse(int warehouseID)
        {
            using (var context = _context)
            {
                var warehouse = context.Warehouses.FirstOrDefault(w => w.IDWarehouse == warehouseID);
                if (warehouse != null)
                {
                    var equipmentHistory = context.EquipmentHistories.Where(eh => eh.WarehouseID == warehouseID);
                    context.EquipmentHistories.RemoveRange(equipmentHistory);

                    var equipment = context.Equipments.Where(e => e.WarehouseID == warehouseID).ToList();
                    context.Equipments.RemoveRange(equipment);
                    context.SaveChanges();

                    var firm = context.Firms.FirstOrDefault(f => f.WarehouseID == warehouseID);
                    if (firm != null)
                    {
                        firm.WarehouseID = null;
                        context.SaveChanges();
                    }

                    context.Warehouses.Remove(warehouse);
                    context.SaveChanges();
                }
            }
        }

        public UpdateEquipmentResponse UpdateEquipment(UpdateEquipmentRequest request)
        {
            using (var context = _context)
            {
                UpdateEquipmentResponse response = new UpdateEquipmentResponse();
                var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == request.IDEquipment);

                if (equipment != null)
                {
                    equipment.Base64Image = request.Base64Image;
                    equipment.EquipmentName = request.EquipmentName;
                    equipment.Quantity = request.Quantity;
                    equipment.EquipmentDescription = request.EquipmentDescription;
                    context.SaveChanges();

                    response.IsSuccessful = true;
                    response.Message = null;

                    return response;
                }

                response.IsSuccessful = false;
                response.Message = "No equipment found to update";
                return response;
            }
        }

        public void DeleteEquipment(int equipmentID)
        {
            using (var context = _context)
            {
                var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == equipmentID);
                if (equipment != null)
                {
                    var equipmentHistory = context.EquipmentHistories.Where(eh => eh.EquipmentID == equipmentID).ToList();
                    context.EquipmentHistories.RemoveRange(equipmentHistory);
                    context.SaveChanges();

                    context.Equipments.Remove(equipment);
                    context.SaveChanges();
                }
            }
        }

        public List<EquipmentHistoryWarehouseVM> GetEquipmentHistoryWarehouse(int warehouseID)
        {
            using (var context = _context)
            {
                var equipmentHistoryGroup = context.EquipmentHistories
                    .Where(eh => eh.WarehouseID == warehouseID)
                    .GroupBy(eh => new { eh.EmployeeID, eh.DateEquipmentTaken })
                    .Select(eh => eh.ToList());

                var test = new List<EquipmentHistoryWarehouseVM>();

                foreach (var group in equipmentHistoryGroup)
                {
                    foreach (var eh in group)
                    {
                        var equipmentListVM = new List<EquipmentVM>();
                        equipmentListVM.Add(new EquipmentVM
                        {
                            Base64Image = eh.Equipment.Base64Image,
                            EquipmentDescription = eh.Equipment.EquipmentDescription,
                            EquipmentName = eh.Equipment.EquipmentName,
                            Quantity = eh.Equipment.Quantity,
                            IDEquipment = eh.Equipment.IDEquipment
                        });

                        test.Add(new EquipmentHistoryWarehouseVM
                        {
                            WarehouseID = warehouseID,
                            DateEquipmentTaken = eh.DateEquipmentTaken,
                            Employee = new EmployeeVM(eh.EmployeeID, eh.Employee.FirebaseID, eh.Employee.FullName, eh.Employee.Email,
                            eh.Employee.Phone, eh.Employee.FirmID, eh.Employee.EmployeeTypeID, eh.Employee.ConstructionSiteID),
                            Equipment = equipmentListVM
                        });

                    }
                }

                return test;
            }
        }

        public IEnumerable<EquipmentHistoryVM> GetEquipmentHistoryForEmployeeAndDate(GetEquipmentHistoryForEmployeeAndDateRequest request)
        {
            using (var context = _context)
            {
                var equipmentHistoryVM = new List<EquipmentHistoryVM>();
                var equipmentHistory = context.EquipmentHistories.Where(eh => eh.DateEquipmentTaken == request.DateEquipmentTaken && eh.EmployeeID == request.EmployeeID);

                if (equipmentHistory != null)
                {
                    foreach (var eq in equipmentHistory)
                    {
                        var employee = eq.Employee;
                        var employeeVM = new EmployeeVM(
                            employee.IDEmployee, employee.FirebaseID,
                            employee.FullName, employee.Email, employee.Phone, employee.FirmID,
                            employee.EmployeeTypeID, employee.ConstructionSiteID
                        );

                        var equipment = eq.Equipment;
                        var equipmentVM = new EquipmentVM
                        {
                            IDEquipment = equipment.IDEquipment,
                            Base64Image = equipment.Base64Image,
                            EquipmentName = equipment.EquipmentName,
                            Quantity = equipment.Quantity,
                            EquipmentDescription = equipment.EquipmentDescription
                        };

                        equipmentHistoryVM.Add(new EquipmentHistoryVM
                        {
                            Equipment = equipmentVM,
                            Employee = employeeVM,
                            DateEquipmentTaken = eq.DateEquipmentTaken,
                            QuantityTaken = eq.QuantityTaken,
                            WarehouseID = eq.WarehouseID
                        });
                    }
                }

                return equipmentHistoryVM;
            }
        }

        public void DeleteEquipmentHistory(DeleteEquipmentHistoryRequest request)
        {
            using (var context = _context)
            {
                var equipmentHistory = context.EquipmentHistories.Where(eh => eh.EmployeeID == request.EmployeeID && eh.DateEquipmentTaken == request.DateEquipmentTaken);
                if (equipmentHistory != null)
                {
                    foreach (var eq in equipmentHistory)
                    {
                        var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == eq.EquipmentID);
                        if (equipment != null)
                        {
                            equipment.Quantity += eq.QuantityTaken;
                        }
                    }

                    context.EquipmentHistories.RemoveRange(equipmentHistory);
                    context.SaveChanges();
                }
            }
        }

        public void UpdateEquipmentHistory(UpdateEquipmentHistoryRequest request)
        {
            using (var context = _context)
            {
                var equipmentHistory = context.EquipmentHistories.Where(eh => eh.EmployeeID == request.OldEmployeeID && eh.DateEquipmentTaken == request.DateEquipmentTaken);
                var requestEquipmentHistoryDiff = request.EquipmentHistory.Where(rehd => !equipmentHistory.Any(eh => eh.EquipmentID == rehd.EquipmentID));
                if (equipmentHistory != null)
                {
                    foreach (var eq in equipmentHistory)
                    {
                        var eqh = request.EquipmentHistory.FirstOrDefault(req => req.EquipmentID == eq.EquipmentID);
                        if (eqh != null)
                        {
                            var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == eq.EquipmentID);

                            if (eqh.QuantityTaken > eq.QuantityTaken)
                            {
                                int quantityDifference = eqh.QuantityTaken - eq.QuantityTaken;
                                eq.QuantityTaken = eq.QuantityTaken + quantityDifference;
                                equipment.Quantity -= quantityDifference;
                            }
                            else if (eqh.QuantityTaken < eq.QuantityTaken)
                            {
                                int quantityDifference = eq.QuantityTaken - eqh.QuantityTaken;
                                eq.QuantityTaken = eq.QuantityTaken - quantityDifference;
                                equipment.Quantity += quantityDifference;
                            }
                            eq.EmployeeID = request.NewEmployeeID;
                        }
                        else
                        {
                            var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == eq.EquipmentID);
                            if (equipment != null)
                            {
                                equipment.Quantity += eq.QuantityTaken;
                            }
                            context.EquipmentHistories.Remove(eq);
                        }
                    }
                    if (requestEquipmentHistoryDiff != null)
                    {
                        foreach (var rehd in requestEquipmentHistoryDiff)
                        {
                            var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == rehd.EquipmentID);
                            if (equipment != null)
                            {
                                equipment.Quantity -= rehd.QuantityTaken;
                            }

                            context.EquipmentHistories.Add(new EquipmentHistory
                            {
                                DateEquipmentTaken = request.DateEquipmentTaken,
                                QuantityTaken = rehd.QuantityTaken,
                                EmployeeID = request.NewEmployeeID,
                                EquipmentID = equipment.IDEquipment,
                                WarehouseID = request.WarehouseID
                            });
                        }
                    }
                    context.SaveChanges();
                }
            }
        }
    }
}
