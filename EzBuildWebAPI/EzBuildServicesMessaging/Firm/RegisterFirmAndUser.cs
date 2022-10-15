using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;

namespace EzBuildServicesMessaging.Firm
{
    public class RegisterFirmAndUserRequest : BaseRequest
    {
        public string UserFullName { get; set; }
        public string UserFirebaseUID { get; set; }
        public string UserEmail { get; set; }
        public string FirmName { get; set; }
    }

    public class RegisterFirmAndUserResponse : BaseResponse
    {
        public EmployeeVM Employee { get; set; }
    }
}
