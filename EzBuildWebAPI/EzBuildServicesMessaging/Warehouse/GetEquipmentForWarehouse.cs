using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Warehouse
{
    public class GetEquipmentForWarehouseResponse : BaseResponse
    {
        public IEnumerable<EquipmentVM> Equipment { get; set; }
    }
}
