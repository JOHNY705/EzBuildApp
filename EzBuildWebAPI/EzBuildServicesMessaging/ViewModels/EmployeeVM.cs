namespace EzBuildServicesMessaging.ViewModels
{
    public class EmployeeVM
    {
        public EmployeeVM(int idEmployee, string firebaseId, string fullName, string email, string phone, int firmId, int employeeTypeId, int? constructionSiteId)
        {
            IDEmployee = idEmployee;
            FirebaseID = firebaseId;
            FullName = fullName;
            Email = email;
            Phone = phone;
            FirmID = firmId;
            EmployeeTypeID = employeeTypeId;
            ConstructionSiteID = constructionSiteId;
        }

        public int IDEmployee { get; set; }
        public string FirebaseID { get; set; }
        public string FullName { get; set; }
        public string Email { get; set; }
        public string Phone { get; set; }
        public int FirmID { get; set; }
        public int EmployeeTypeID { get; set; }
        public int? ConstructionSiteID { get; set; }
    }
}
