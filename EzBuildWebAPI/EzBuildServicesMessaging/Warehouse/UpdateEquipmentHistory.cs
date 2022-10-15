using EzBuildServicesMessaging.Base;
using System;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Warehouse
{
    public class UpdateEquipmentHistoryRequest : BaseRequest
    {
        public IEnumerable<EquipmentHistory> EquipmentHistory { get; set; }
        public int OldEmployeeID { get; set; }
        public int NewEmployeeID { get; set; }
        public DateTime DateEquipmentTaken { get; set; }
        public int WarehouseID { get; set; }
    }

    public class UpdateEquipmentHistoryResponse : BaseResponse
    {
    }
}
