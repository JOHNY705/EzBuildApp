using EzBuildServicesMessaging.Base;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.EmployeeHours
{
    public class CreateEmployeeHoursForDayRequest : BaseRequest
    {
        public IEnumerable<EmployeeHours> EmployeeHours { get; set; }
    }

    public class CreateEmployeeHoursForDayResponse : BaseResponse
    {
    }
}
