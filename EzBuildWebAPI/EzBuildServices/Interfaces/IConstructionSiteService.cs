using EzBuildServicesMessaging.ConstructionSites;

namespace EzBuildServices.Interfaces
{
    public interface IConstructionSiteService
    {
        GetConstructionSitesForFirmResponse GetConstructionSitesForFirm(int firmID);
        GetConstructionSitesForFirmResponse GetConstructionSitesForFirmNoImage(int firmID);
        GetConstructionSitesForFirmResponse GetConstructionSitesForEmployee(int employeeID);
        GetConstructionSiteResponse GetConstructionSite(int constructionSiteID);
        UpdateConstructionSiteResponse UpdateConstructionSite(UpdateConstructionSiteRequest request);
        UpdateConstructionSiteImageResponse UpdateConstructionSiteImageRequest(UpdateConstructionSiteImageRequest request);
        CreateConstructionSiteResponse CreateConstructionSite(CreateConstructionSiteRequest request);
        RemoveEmployeeFromConstructionSiteResponse RemoveEmployeeFromConstructionSite(RemoveEmployeeFromConstructionSiteRequest request);
        UpdateCSManagerResponse UpdateCSManagerForConstructionSite(UpdateCSManagerRequest request);
        DeleteConstructionSiteResponse DeleteConstructionSite(int constructionSiteID);
        CreateCSDiaryEntryResponse CreateCSDiaryEntry(CreateCSDiaryEntryRequest request);
        DeleteCSDiaryEntryResponse DeleteCSDiaryEntry(int diaryEntryID);
        UpdateCSDiaryEntryResponse UpdateCSDiaryEntry(UpdateCSDiaryEntryRequest request);
    }
}
