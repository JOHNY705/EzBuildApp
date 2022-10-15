using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;

namespace EzBuildServicesMessaging.Employees
{
    public class GetRegisteredEmployeeDetailsResponse : BaseResponse
    {
        public EmployeeVM Employee { get; set; }
    }
}
