using System;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.ViewModels
{
    public class EquipmentHistoryWarehouseVM
    {
        public EmployeeVM Employee { get; set; }
        public DateTime DateEquipmentTaken { get; set; }
        public List<EquipmentVM> Equipment { get; set; }
        public int WarehouseID { get; set; }
    }
}
