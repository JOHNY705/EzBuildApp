using System;

namespace EzBuildServicesMessaging.ViewModels
{
    public class EmployeeHoursMonthVM
    {
        public EmployeeHoursMonthVM(short hoursWorked, DateTime dateWorkDone, int employeeID)
        {
            HoursWorked = hoursWorked;
            DateWorkDone = dateWorkDone;
            EmployeeID = employeeID;
        }

        public short HoursWorked { get; set; }
        public DateTime DateWorkDone { get; set; }
        public int EmployeeID { get; set; }
    }
}
