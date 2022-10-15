using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Warehouse
{
    public class GetEquipmentHistoryWarehouseResponse : BaseResponse
    {
        public List<EquipmentHistoryWarehouseVM> EquipmentHistory { get; set; }
    }
}
