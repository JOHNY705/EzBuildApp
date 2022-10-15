using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class GetConstructionSiteResponse : BaseResponse
    {
        public ConstructionSiteVM ConstructionSite { get; set; }
    }
}
