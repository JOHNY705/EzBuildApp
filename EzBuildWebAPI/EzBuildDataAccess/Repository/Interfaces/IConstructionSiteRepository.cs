using EzBuildServicesMessaging.ConstructionSites;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildDataAccess.Repository.Interfaces
{
    public interface IConstructionSiteRepository
    {
        IEnumerable<ConstructionSiteVM> GetConstructionSitesForFirm(int firmID);
        IEnumerable<ConstructionSiteVM> GetConstructionSitesForFirmNoImage(int firmID);
        IEnumerable<ConstructionSiteVM> GetConstructionSitesForEmployee(int employeeID);
        ConstructionSiteVM GetConstructionSite(int constructionSiteID);
        ConstructionSiteVM UpdateConstructionSite(UpdateConstructionSiteRequest request);
        void UpdateConstructionSiteImage(UpdateConstructionSiteImageRequest request);
        void CreateConstructionSite(CreateConstructionSiteRequest request);
        void RemoveEmployeeFromConstructionSite(RemoveEmployeeFromConstructionSiteRequest request);
        void UpdateCSManagerForConstructionSite(UpdateCSManagerRequest request);
        void DeleteConstructionSite(int constructionSiteID);
        void CreateCSDiaryEntry(CreateCSDiaryEntryRequest request);
        void DeleteCSDiaryEntry(int diaryEntryID);
        void UpdateCSDiaryEntry(UpdateCSDiaryEntryRequest request);
    }
}
