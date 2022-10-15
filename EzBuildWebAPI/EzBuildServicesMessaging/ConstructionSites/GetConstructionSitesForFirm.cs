using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class GetConstructionSitesForFirmResponse : BaseResponse
    {
        public IEnumerable<ConstructionSiteVM> ConstructionSites { get; set; }
    }
}
