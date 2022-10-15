using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.EmployeeHours
{
    public class GetEmployeeHoursMonthForEmployeeResponse : BaseResponse
    {
        public IEnumerable<EmployeeHoursMonthVM> EmployeeHours { get; set; }
    }
}
