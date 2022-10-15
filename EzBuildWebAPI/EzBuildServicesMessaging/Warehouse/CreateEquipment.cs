using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.Warehouse
{
    public class CreateEquipmentRequest : BaseRequest
    {
        public string Base64Image { get; set; }
        public string EquipmentName { get; set; }
        public int Quantity { get; set; }
        public string EquipmentDescription { get; set; }
        public int WarehouseID { get; set; }
    }

    public class CreateEquipmentResponse : BaseResponse
    {
    }
}
