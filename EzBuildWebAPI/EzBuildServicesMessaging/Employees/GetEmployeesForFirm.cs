using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Employees
{
    public class GetEmployeesForFirmResponse : BaseResponse
    {
        public IEnumerable<EmployeeVM> Employees { get; set; }
    }
}
