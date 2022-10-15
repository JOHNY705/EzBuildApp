using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class UpdateConstructionSiteRequest : BaseRequest
    {
        public int IDConstructionSite { get; set; }
        public string Base64Image { get; set; }
        public string FullAddress { get; set; }
        public decimal Latitude { get; set; }
        public decimal Longitude { get; set; }
        public int? ConstructionSiteManagerID { get; set; }
        public IEnumerable<int> Employees { get; set; }
        public bool IsActive { get; set; }
    }

    public class UpdateConstructionSiteResponse : BaseResponse
    {
        public ConstructionSiteVM ConstructionSite { get; set; }
    }
}
