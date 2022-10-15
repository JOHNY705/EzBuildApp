using EzBuildDataAccess;
using EzBuildDataAccess.Repository.Factory;
using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Firm;
using EzBuildServicesMessaging.ViewModels;
using System;

namespace EzBuildServices.Implementations
{
    class FirmService : IFirmService
    {
        private readonly IFirmRepository firmRepository = RepositoryFactory.GetFirmRepository();

        public GetFirmNameResponse GetFirmName(int firmID)
        {
            GetFirmNameResponse response = new GetFirmNameResponse();

            try
            {
                response.FirmName = firmRepository.GetFirmName(firmID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public RegisterFirmAndUserResponse RegisterFirmAndUser(RegisterFirmAndUserRequest request)
        {
            RegisterFirmAndUserResponse response = new RegisterFirmAndUserResponse();

            try
            {
                Employee employee = firmRepository.RegisterFirmAndUser(request);

                EmployeeVM employeeVM = new EmployeeVM(
                    employee.IDEmployee, employee.FirebaseID, employee.FullName,
                    employee.Email, employee.Phone, employee.FirmID, employee.EmployeeTypeID,
                    employee.ConstructionSiteID);

                response.Employee = employeeVM;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateFirmNameResponse UpdateFirmName(UpdateFirmNameRequest request)
        {
            UpdateFirmNameResponse response = new UpdateFirmNameResponse();

            try
            {
                firmRepository.UpdateFirmName(request);
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
