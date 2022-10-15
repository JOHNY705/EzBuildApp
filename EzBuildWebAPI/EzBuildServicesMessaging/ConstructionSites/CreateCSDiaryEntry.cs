using EzBuildServicesMessaging.Base;
using System;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class CreateCSDiaryEntryRequest : BaseRequest
    {
        public string DiaryEntry { get; set; }
        public DateTime DiaryEntryDate { get; set; }
        public int DiaryEntryEmployeeID { get; set; }
        public int ConstructionSiteID { get; set; }
    }

    public class CreateCSDiaryEntryResponse : BaseResponse
    {
    }
}
