using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class RemoveEmployeeFromConstructionSiteRequest : BaseRequest
    {
        public int IDEmployee { get; set; }
        public int IDConstructionSite { get; set; }
    }

    public class RemoveEmployeeFromConstructionSiteResponse : BaseResponse
    {
    }
}
