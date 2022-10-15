using EzBuildServicesMessaging.Base;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class CreateConstructionSiteRequest : BaseRequest
    {
        public string Base64Image { get; set; }
        public string FullAddress { get; set; }
        public decimal Latitude { get; set; }
        public decimal Longitude { get; set; }
        public int? ConstructionSiteManagerID { get; set; }
        public int FirmID { get; set; }
        public IEnumerable<int> Employees { get; set; }
        public bool IsActive { get; set; }
    }

    public class CreateConstructionSiteResponse : BaseResponse
    {
    }
}
