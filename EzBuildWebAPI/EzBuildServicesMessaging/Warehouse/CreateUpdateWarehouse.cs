using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.Warehouse
{
    public class CreateUpdateWarehouseRequest : BaseRequest
    {
        public int FirmID { get; set; }
        public string FullAddress { get; set; }
        public int WarehouseManagerID { get; set; }
    }

    public class CreateUpdateWarehouseResponse : BaseResponse
    {
    }
}
