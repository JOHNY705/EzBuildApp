using EzBuildServicesMessaging.Base;

namespace EzBuildServicesMessaging.Employees
{
    public class CreateEmployeeRequest : BaseRequest
    {
        public string FirebaseID { get; set; }
        public string FullName { get; set; }
        public string Email { get; set; }
        public string Phone { get; set; }
        public int EmployeeTypeID { get; set; }
        public int FirmID { get; set; }
    }

    public class CreateEmployeeResponse : BaseResponse
    {
    }
}
