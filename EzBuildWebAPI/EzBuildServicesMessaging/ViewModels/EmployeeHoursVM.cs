using System;

namespace EzBuildServicesMessaging.ViewModels
{
    public class EmployeeHoursVM
    {
        public int IDEmployeeHours { get; set; }
        public short HoursWorked { get; set; }
        public DateTime DateWorkDone { get; set; }
        public int EmployeeID { get; set; }
        public string EmployeeFullName { get; set; }
    }
}
