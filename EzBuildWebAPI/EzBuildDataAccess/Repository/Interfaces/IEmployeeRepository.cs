using EzBuildServicesMessaging.Employees;
using System.Collections.Generic;

namespace EzBuildDataAccess.Repository.Interfaces
{
    public interface IEmployeeRepository
    {
        IEnumerable<Employee> GetEmployeesForFirm(int firmID);
        IEnumerable<Employee> GetEmployeesForConstructionSite(int constructionSiteID);
        IEnumerable<Employee> GetEngineersForFirm(int firmID);
        IEnumerable<Employee> GetPhysicalWorkersForFirm(int firmID);
        IEnumerable<Employee> GetWarehouseManagerForFirm(int firmID);
        IEnumerable<Employee> GetUnassignedEmployeesForFirm(int firmID);
        Employee GetRegisteredEmployeeDetails(string firebaseUID);
        void CreateEmployee(CreateEmployeeRequest request);
        void UpdateEmployee(UpdateEmployeeRequest request);
        void DeleteEmployee(int employeeID);
    }
}
