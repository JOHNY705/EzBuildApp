using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Employees
{
    public class GetEngineersResponse : BaseResponse
    {
        public IEnumerable<EmployeeVM> Employees { get; set; }
    }
}
