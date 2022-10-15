using System;

namespace EzBuildServicesMessaging.ViewModels
{
    public class EquipmentHistoryVM
    {
        public EmployeeVM Employee { get; set; }
        public DateTime DateEquipmentTaken { get; set; }
        public int QuantityTaken { get; set; }
        public EquipmentVM Equipment { get; set; }
        public int WarehouseID { get; set; }
    }
}
