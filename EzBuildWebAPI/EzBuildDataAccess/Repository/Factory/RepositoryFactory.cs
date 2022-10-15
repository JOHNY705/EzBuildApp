using EzBuildDataAccess.Repository.Implementations;
using EzBuildDataAccess.Repository.Interfaces;

namespace EzBuildDataAccess.Repository.Factory
{
    public static class RepositoryFactory
    {
        public static IEmployeeRepository GetEmployeeRepository() => new EmployeeRepository();
        public static IMeetingRepository GetMeetingRepository() => new MeetingRepository();
        public static IConstructionSiteRepository GetConstructionSiteRepository() => new ConstructionSiteRepository();
        public static IEmployeeHoursRepository GetEmployeeHoursRepository() => new EmployeeHoursRepository();
        public static IFirmRepository GetFirmRepository() => new FirmRepository();
        public static IWarehouseRepository GetWarehouseRepository() => new WarehouseRepository();
    }
}
