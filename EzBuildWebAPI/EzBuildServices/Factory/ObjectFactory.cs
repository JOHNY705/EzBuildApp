using EzBuildServices.Implementations;
using EzBuildServices.Interfaces;

namespace EzBuildServices.Factory
{
    public static class ObjectFactory
    {
        public static IEmployeeService GetEmployeeService() => new EmployeeService();
        public static IMeetingService GetMeetingService() => new MeetingService();
        public static IConstructionSiteService GetConstructionSiteService() => new ConstructionSiteService();
        public static IEmployeeHoursService GetEmployeeHoursService() => new EmployeeHoursService();
        public static IFirmService GetFirmService() => new FirmService();
        public static IWarehouseService GetWarehouseService() => new WarehouseService();
    }
}
