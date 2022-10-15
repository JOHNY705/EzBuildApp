using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.EmployeeHours
{
    public class GetEmployeeHoursForCSResponse : BaseResponse
    {
        public IEnumerable<EmployeeHoursVM> EmployeeHours { get; set; }
    }
}
