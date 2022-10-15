using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Warehouse
{
    public class GetEquipmentHistoryForEmployeeAndDateRequest : BaseRequest
    {
        public int EmployeeID { get; set; }
        public DateTime DateEquipmentTaken { get; set; }
    }

    public class GetEquipmentHistoryForEmployeeAndDateResponse : BaseResponse
    {
        public IEnumerable<EquipmentHistoryVM> EquipmentHistory { get; set; }
    }
}
