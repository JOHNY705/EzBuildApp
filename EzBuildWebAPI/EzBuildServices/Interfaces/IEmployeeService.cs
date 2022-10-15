using EzBuildServicesMessaging.Employees;

namespace EzBuildServices.Interfaces
{
    public interface IEmployeeService
    {
        GetEmployeesForFirmResponse GetEmployeesForFirm(int firmID);
        GetEmployeesForConstructionSiteResponse GetEmployeesForConstructionSite(int constructionSiteID);
        GetEngineersResponse GetEngineersForFirm(int firmID);
        GetEmployeesForFirmResponse GetPhysicalWorkersForFirm(int firmID);
        GetEmployeesForFirmResponse GetWarehouseManagerForFirm(int firmID);
        GetUnassignedEmployeesResponse GetUnassignedEmployeesForFirm(int firmID);
        GetRegisteredEmployeeDetailsResponse GetRegisteredEmployeeDetails(string firebaseUID);
        CreateEmployeeResponse CreateEmployee(CreateEmployeeRequest request);
        UpdateEmployeeResponse UpdateEmployee(UpdateEmployeeRequest request);
        DeleteEmployeeResponse DeleteEmployee(int employeeID);
    }
}
