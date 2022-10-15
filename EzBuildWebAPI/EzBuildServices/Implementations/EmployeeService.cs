using EzBuildDataAccess;
using EzBuildDataAccess.Repository.Factory;
using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Employees;
using EzBuildServicesMessaging.ViewModels;
using System;
using System.Collections.Generic;

namespace EzBuildServices.Implementations
{
    class EmployeeService : IEmployeeService
    {
        private readonly IEmployeeRepository employeeRepository = RepositoryFactory.GetEmployeeRepository();

        public GetEmployeesForFirmResponse GetEmployeesForFirm(int firmID)
        {
            GetEmployeesForFirmResponse response = new GetEmployeesForFirmResponse();

            try
            {
                IEnumerable<Employee> employees = employeeRepository.GetEmployeesForFirm(firmID);

                ICollection<EmployeeVM> employeeVMList = new List<EmployeeVM>();
                foreach (Employee e in employees)
                {
                    employeeVMList.Add(new EmployeeVM(e.IDEmployee, e.FirebaseID, e.FullName, e.Email, e.Phone, e.FirmID, e.EmployeeTypeID, e.ConstructionSiteID));
                }

                response.Employees = employeeVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeesForConstructionSiteResponse GetEmployeesForConstructionSite(int constructionSiteID)
        {
            GetEmployeesForConstructionSiteResponse response = new GetEmployeesForConstructionSiteResponse();

            try
            {
                IEnumerable<Employee> employees = employeeRepository.GetEmployeesForConstructionSite(constructionSiteID);

                ICollection<EmployeeVM> employeeVMList = new List<EmployeeVM>();
                foreach (Employee e in employees)
                {
                    employeeVMList.Add(new EmployeeVM(e.IDEmployee, e.FirebaseID, e.FullName, e.Email, e.Phone, e.FirmID, e.EmployeeTypeID, e.ConstructionSiteID));
                }

                response.Employees = employeeVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEngineersResponse GetEngineersForFirm(int firmID)
        {
            GetEngineersResponse response = new GetEngineersResponse();

            try
            {
                IEnumerable<Employee> employees = employeeRepository.GetEngineersForFirm(firmID);

                ICollection<EmployeeVM> employeeVMList = new List<EmployeeVM>();
                foreach (Employee e in employees)
                {
                    employeeVMList.Add(new EmployeeVM(e.IDEmployee, e.FirebaseID, e.FullName, e.Email, e.Phone, e.FirmID, e.EmployeeTypeID, e.ConstructionSiteID));
                }

                response.Employees = employeeVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeesForFirmResponse GetPhysicalWorkersForFirm(int firmID)
        {
            GetEmployeesForFirmResponse response = new GetEmployeesForFirmResponse();

            try
            {
                IEnumerable<Employee> employees = employeeRepository.GetPhysicalWorkersForFirm(firmID);

                ICollection<EmployeeVM> employeeVMList = new List<EmployeeVM>();
                foreach (Employee e in employees)
                {
                    employeeVMList.Add(new EmployeeVM(e.IDEmployee, e.FirebaseID, e.FullName, e.Email, e.Phone, e.FirmID, e.EmployeeTypeID, e.ConstructionSiteID));
                }

                response.Employees = employeeVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetEmployeesForFirmResponse GetWarehouseManagerForFirm(int firmID)
        {
            GetEmployeesForFirmResponse response = new GetEmployeesForFirmResponse();

            try
            {
                IEnumerable<Employee> employees = employeeRepository.GetWarehouseManagerForFirm(firmID);

                ICollection<EmployeeVM> employeeVMList = new List<EmployeeVM>();
                foreach (Employee e in employees)
                {
                    employeeVMList.Add(new EmployeeVM(e.IDEmployee, e.FirebaseID, e.FullName, e.Email, e.Phone, e.FirmID, e.EmployeeTypeID, e.ConstructionSiteID));
                }

                response.Employees = employeeVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetUnassignedEmployeesResponse GetUnassignedEmployeesForFirm(int firmID)
        {
            GetUnassignedEmployeesResponse response = new GetUnassignedEmployeesResponse();

            try
            {
                IEnumerable<Employee> employees = employeeRepository.GetUnassignedEmployeesForFirm(firmID);

                ICollection<EmployeeVM> employeeVMList = new List<EmployeeVM>();
                foreach (Employee e in employees)
                {
                    employeeVMList.Add(new EmployeeVM(e.IDEmployee, e.FirebaseID, e.FullName, e.Email, e.Phone, e.FirmID, e.EmployeeTypeID, e.ConstructionSiteID));
                }

                response.Employees = employeeVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetRegisteredEmployeeDetailsResponse GetRegisteredEmployeeDetails(string firebaseUID)
        {
            GetRegisteredEmployeeDetailsResponse response = new GetRegisteredEmployeeDetailsResponse();

            try
            {
                Employee employee = employeeRepository.GetRegisteredEmployeeDetails(firebaseUID);

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

        public CreateEmployeeResponse CreateEmployee(CreateEmployeeRequest request)
        {
            CreateEmployeeResponse response = new CreateEmployeeResponse();

            try
            {
                employeeRepository.CreateEmployee(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateEmployeeResponse UpdateEmployee(UpdateEmployeeRequest request)
        {
            UpdateEmployeeResponse response = new UpdateEmployeeResponse();

            try
            {
                employeeRepository.UpdateEmployee(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteEmployeeResponse DeleteEmployee(int employeeID)
        {
            DeleteEmployeeResponse response = new DeleteEmployeeResponse();

            try
            {
                employeeRepository.DeleteEmployee(employeeID);
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
