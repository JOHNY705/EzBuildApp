using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.Warehouse
{
    public class UpdateEquipmentRequest : BaseRequest
    {
        public int IDEquipment { get; set; }
        public string Base64Image { get; set; }
        public string EquipmentName { get; set; }
        public int Quantity { get; set; }
        public string EquipmentDescription { get; set; }
    }

    public class UpdateEquipmentResponse : BaseResponse
    {
    }
}
