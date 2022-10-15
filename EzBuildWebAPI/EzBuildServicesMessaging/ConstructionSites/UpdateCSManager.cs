using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class UpdateCSManagerRequest : BaseRequest
    {
        public int IDEmployee { get; set; }
        public int IDConstructionSite { get; set; }
    }

    public class UpdateCSManagerResponse : BaseResponse
    {
    }
}
