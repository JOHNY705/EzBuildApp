using System.Collections.Generic;

namespace EzBuildServicesMessaging.ViewModels
{
    public class ConstructionSiteVM
    {
        public int IDConstructionSite { get; set; }
        public string Base64Image { get; set; }
        public string FullAddress { get; set; }
        public decimal Latitude { get; set; }
        public decimal Longitude { get; set; }
        public bool IsActive { get; set; }
        public EmployeeVM ConstructionSiteManager { get; set; }
        public IEnumerable<ConstructionSiteDiaryEntryVM> ConstructionSiteDiaryEntries { get; set; }
        public IEnumerable<EmployeeVM> Employees { get; set; }
    }
}
