using System;

namespace EzBuildServicesMessaging.ViewModels
{
    public class ConstructionSiteDiaryEntryVM
    {
        public int IDConstructionSiteDiary { get; set; }
        public string DiaryEntry { get; set; }
        public DateTime DiaryEntryDate { get; set; }
        public string EmployeeFullName { get; set; }
        public int ConstructionSiteID { get; set; }
    }
}
