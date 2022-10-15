using EzBuildServicesMessaging.EmployeeHours;

namespace EzBuildServices.Interfaces
{
    public interface IEmployeeHoursService
    {
        CreateEmployeeHoursForDayResponse CreateEmployeeHoursForDay(CreateEmployeeHoursForDayRequest request);
        GetEmployeeHoursForFirmResponse GetEmployeeHoursForFirm(int firmID);
        GetEmployeeHoursMonthForEmployeeResponse GetEmployeeHoursMonthForEmployee(int employeeID);
        GetEmployeeHoursForCSResponse GetEmployeeHoursForCS(int constructionSiteID);
        GetEmployeeHoursForFirmResponse GetEmployeeHoursForEngineers(int firmID);
        GetEmployeeHoursForFirmResponse GetEmployeeHoursForPhysicalWorkers(int firmID);
        GetEmployeeHoursForFirmResponse GetEmployeeHoursForWarehouseManager(int firmID);

    }
}
