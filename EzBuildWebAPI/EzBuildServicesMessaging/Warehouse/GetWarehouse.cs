using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;

namespace EzBuildServicesMessaging.Warehouse
{
    public class GetWarehouseResponse : BaseResponse
    {
        public WarehouseVM Warehouse { get; set; }
    }
}
