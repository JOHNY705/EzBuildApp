using EzBuildServicesMessaging.EmployeeHours;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildDataAccess.Repository.Interfaces
{
    public interface IEmployeeHoursRepository
    {
        void CreateEmployeeHoursForDay(CreateEmployeeHoursForDayRequest request);
        IEnumerable<EmployeeHoursVM> GetEmployeeHoursForFirm(int firmID);
        IEnumerable<EmployeeHoursMonthVM> GetEmployeeHoursMonthForEmployee(int employeeID);
        IEnumerable<EmployeeHoursVM> GetEmployeeHoursForCS(int constructionSiteID);
        IEnumerable<EmployeeHoursVM> GetEmployeeHoursForEngineers(int firmID);
        IEnumerable<EmployeeHoursVM> GetEmployeeHoursForPhysicalWorkers(int firmID);
        IEnumerable<EmployeeHoursVM> GetEmployeeHoursForWarehouseManager(int firmID);
    }
}
