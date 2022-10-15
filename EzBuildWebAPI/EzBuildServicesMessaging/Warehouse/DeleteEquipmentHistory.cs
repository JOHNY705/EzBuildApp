using EzBuildServicesMessaging.Base;
using System;

namespace EzBuildServicesMessaging.Warehouse
{
    public class DeleteEquipmentHistoryRequest : BaseRequest
    {
        public int EmployeeID { get; set; }
        public DateTime DateEquipmentTaken { get; set; }
    }

    public class DeleteEquipmentHistoryResponse : BaseResponse
    {
    }
}
