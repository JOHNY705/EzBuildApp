using EzBuildDataAccess.Repository.Factory;
using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.EmployeeHours;
using System;

namespace EzBuildServices.Implementations
{
    class EmployeeHoursService : IEmployeeHoursService
    {
        private readonly IEmployeeHoursRepository employeeHoursRepository = RepositoryFactory.GetEmployeeHoursRepository();

        public CreateEmployeeHoursForDayResponse CreateEmployeeHoursForDay(CreateEmployeeHoursForDayRequest request)
        {
            CreateEmployeeHoursForDayResponse response = new CreateEmployeeHoursForDayResponse();

            try
            {
                employeeHoursRepository.CreateEmployeeHoursForDay(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeeHoursForFirmResponse GetEmployeeHoursForFirm(int firmID)
        {
            GetEmployeeHoursForFirmResponse response = new GetEmployeeHoursForFirmResponse();

            try
            {
                response.EmployeeHours = employeeHoursRepository.GetEmployeeHoursForFirm(firmID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeeHoursMonthForEmployeeResponse GetEmployeeHoursMonthForEmployee(int employeeID)
        {
            GetEmployeeHoursMonthForEmployeeResponse response = new GetEmployeeHoursMonthForEmployeeResponse();

            try
            {
                response.EmployeeHours = employeeHoursRepository.GetEmployeeHoursMonthForEmployee(employeeID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeeHoursForCSResponse GetEmployeeHoursForCS(int constructionSiteID)
        {
            GetEmployeeHoursForCSResponse response = new GetEmployeeHoursForCSResponse();

            try
            {
                response.EmployeeHours = employeeHoursRepository.GetEmployeeHoursForCS(constructionSiteID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeeHoursForFirmResponse GetEmployeeHoursForEngineers(int firmID)
        {
            GetEmployeeHoursForFirmResponse response = new GetEmployeeHoursForFirmResponse();

            try
            {
                response.EmployeeHours = employeeHoursRepository.GetEmployeeHoursForEngineers(firmID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeeHoursForFirmResponse GetEmployeeHoursForPhysicalWorkers(int firmID)
        {
            GetEmployeeHoursForFirmResponse response = new GetEmployeeHoursForFirmResponse();

            try
            {
                response.EmployeeHours = employeeHoursRepository.GetEmployeeHoursForPhysicalWorkers(firmID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeeHoursForFirmResponse GetEmployeeHoursForWarehouseManager(int firmID)
        {
            GetEmployeeHoursForFirmResponse response = new GetEmployeeHoursForFirmResponse();

            try
            {
                response.EmployeeHours = employeeHoursRepository.GetEmployeeHoursForWarehouseManager(firmID);
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
