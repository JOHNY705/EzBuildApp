using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class UpdateConstructionSiteImageRequest : BaseRequest
    {
        public int IDConstructionSite { get; set; }
        public string Base64Image { get; set; }
    }

    public class UpdateConstructionSiteImageResponse : BaseResponse
    {
    }
}
