using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Employees
{
    public class GetEmployeesForConstructionSiteResponse : BaseResponse
    {
        public IEnumerable<EmployeeVM> Employees { get; set; }
    }
}
