using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Warehouse
{
    public class GetEquipmentHistoryForWarehouseResponse : BaseResponse
    {
        public IEnumerable<EquipmentHistoryVM> EquipmentHistory { get; set; }
    }
}
