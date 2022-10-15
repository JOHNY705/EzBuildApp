using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.Employees
{
    public class UpdateEmployeeRequest : BaseRequest
    {
        public int IDEmployee { get; set; }
        public string FullName { get; set; }
        public string Phone { get; set; }
    }

    public class UpdateEmployeeResponse : BaseResponse
    {
    }
}
