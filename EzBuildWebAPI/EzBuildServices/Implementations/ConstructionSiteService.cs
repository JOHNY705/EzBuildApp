using EzBuildDataAccess.Repository.Factory;
using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.ConstructionSites;
using EzBuildServicesMessaging.ViewModels;
using System;
using System.Collections.Generic;

namespace EzBuildServices.Implementations
{
    class ConstructionSiteService : IConstructionSiteService
    {
        private readonly IConstructionSiteRepository constructionSiteRepository = RepositoryFactory.GetConstructionSiteRepository();

        public GetConstructionSitesForFirmResponse GetConstructionSitesForFirm(int firmID)
        {
            GetConstructionSitesForFirmResponse response = new GetConstructionSitesForFirmResponse();

            try
            {
                List<ConstructionSiteVM> constructionSitesVM = (List<ConstructionSiteVM>)constructionSiteRepository.GetConstructionSitesForFirm(firmID);
                response.ConstructionSites = constructionSitesVM;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetConstructionSitesForFirmResponse GetConstructionSitesForFirmNoImage(int firmID)
        {
            GetConstructionSitesForFirmResponse response = new GetConstructionSitesForFirmResponse();

            try
            {
                List<ConstructionSiteVM> constructionSitesVM = (List<ConstructionSiteVM>)constructionSiteRepository.GetConstructionSitesForFirmNoImage(firmID);
                response.ConstructionSites = constructionSitesVM;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetConstructionSitesForFirmResponse GetConstructionSitesForEmployee(int employeeID)
        {
            GetConstructionSitesForFirmResponse response = new GetConstructionSitesForFirmResponse();

            try
            {
                List<ConstructionSiteVM> constructionSitesVM = (List<ConstructionSiteVM>)constructionSiteRepository.GetConstructionSitesForEmployee(employeeID);
                response.ConstructionSites = constructionSitesVM;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetConstructionSiteResponse GetConstructionSite(int constructionSiteID)
        {
            GetConstructionSiteResponse response = new GetConstructionSiteResponse();

            try
            {
                response.ConstructionSite = constructionSiteRepository.GetConstructionSite(constructionSiteID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateConstructionSiteResponse UpdateConstructionSite(UpdateConstructionSiteRequest request)
        {
            UpdateConstructionSiteResponse response = new UpdateConstructionSiteResponse();

            try
            {
                response.ConstructionSite = constructionSiteRepository.UpdateConstructionSite(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateConstructionSiteImageResponse UpdateConstructionSiteImageRequest(UpdateConstructionSiteImageRequest request)
        {
            UpdateConstructionSiteImageResponse response = new UpdateConstructionSiteImageResponse();

            try
            {
                constructionSiteRepository.UpdateConstructionSiteImage(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public CreateConstructionSiteResponse CreateConstructionSite(CreateConstructionSiteRequest request)
        {
            CreateConstructionSiteResponse response = new CreateConstructionSiteResponse();

            try
            {
                constructionSiteRepository.CreateConstructionSite(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public RemoveEmployeeFromConstructionSiteResponse RemoveEmployeeFromConstructionSite(RemoveEmployeeFromConstructionSiteRequest request)
        {
            RemoveEmployeeFromConstructionSiteResponse response = new RemoveEmployeeFromConstructionSiteResponse();

            try
            {
                constructionSiteRepository.RemoveEmployeeFromConstructionSite(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateCSManagerResponse UpdateCSManagerForConstructionSite(UpdateCSManagerRequest request)
        {
            UpdateCSManagerResponse response = new UpdateCSManagerResponse();

            try
            {
                constructionSiteRepository.UpdateCSManagerForConstructionSite(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteConstructionSiteResponse DeleteConstructionSite(int constructionSiteID)
        {
            DeleteConstructionSiteResponse response = new DeleteConstructionSiteResponse();

            try
            {
                constructionSiteRepository.DeleteConstructionSite(constructionSiteID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public CreateCSDiaryEntryResponse CreateCSDiaryEntry(CreateCSDiaryEntryRequest request)
        {
            CreateCSDiaryEntryResponse response = new CreateCSDiaryEntryResponse();

            try
            {
                constructionSiteRepository.CreateCSDiaryEntry(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteCSDiaryEntryResponse DeleteCSDiaryEntry(int diaryEntryID)
        {
            DeleteCSDiaryEntryResponse response = new DeleteCSDiaryEntryResponse();

            try
            {
                constructionSiteRepository.DeleteCSDiaryEntry(diaryEntryID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateCSDiaryEntryResponse UpdateCSDiaryEntry(UpdateCSDiaryEntryRequest request)
        {
            UpdateCSDiaryEntryResponse response = new UpdateCSDiaryEntryResponse();

            try
            {
                constructionSiteRepository.UpdateCSDiaryEntry(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }
    }
}
