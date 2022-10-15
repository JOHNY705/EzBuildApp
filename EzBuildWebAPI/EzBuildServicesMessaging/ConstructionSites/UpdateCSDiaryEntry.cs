using EzBuildServicesMessaging.Base;
using System;

namespace EzBuildServicesMessaging.ConstructionSites
{
    public class UpdateCSDiaryEntryRequest : BaseRequest
    {
        public int IDConstructionSiteDiaryEntry { get; set; }
        public string DiaryEntry { get; set; }
        public DateTime DiaryEntryDate { get; set; }
        public int DiaryEntryEmployeeID { get; set; }
    }

    public class UpdateCSDiaryEntryResponse : BaseResponse
    {
    }
}
