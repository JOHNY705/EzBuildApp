using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.Firm
{
    public class UpdateFirmNameRequest : BaseRequest
    {
        public int FirmID { get; set; }
        public string FirmName { get; set; }
    }

    public class UpdateFirmNameResponse : BaseResponse
    {
    }
}
