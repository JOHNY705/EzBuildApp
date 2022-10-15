using EzBuildServicesMessaging.Base;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Warehouse
{
    public class CreateEquipmentHistoryRequest : BaseRequest
    {
        public IEnumerable<EquipmentHistory> EquipmentHistory { get; set; }
        public int EmployeeID { get; set; }
        public int WarehouseID { get; set; }
    }

    public class CreateEquipmentHistoryResponse : BaseResponse
    {
    }
}
