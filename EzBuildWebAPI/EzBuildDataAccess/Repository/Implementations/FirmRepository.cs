using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServicesMessaging.Firm;
using System.Linq;

namespace EzBuildDataAccess.Repository.Implementations
{
    class FirmRepository : IFirmRepository
    {
        private readonly EzBuildDBEntities _context = new EzBuildDBEntities();

        public string GetFirmName(int firmID)
        {
            using (var context = _context)
            {
                return context.Firms.FirstOrDefault(f => f.IDFirm == firmID).FirmName;
            }
        }

        public Employee RegisterFirmAndUser(RegisterFirmAndUserRequest request)
        {
            using (var context = _context)
            {
                Firm firm = new Firm
                {
                    FirmName = request.FirmName,
                };

                context.Firms.Add(firm);
                context.SaveChanges();

                int firmID = firm.IDFirm;

                Employee employee = new Employee
                {
                    FirebaseID = request.UserFirebaseUID,
                    FullName = request.UserFullName,
                    Email = request.UserEmail,
                    EmployeeTypeID = 1,
                    FirmID = firmID,
                };


                context.Employees.Add(employee);
                context.SaveChanges();

                return context.Employees.FirstOrDefault(e => e.IDEmployee == employee.IDEmployee);
            }
        }

        public void UpdateFirmName(UpdateFirmNameRequest request)
        {
            using (var context = _context)
            {
                var firm = context.Firms.FirstOrDefault(f => f.IDFirm == request.FirmID);
                if (firm != null)
                {
                    firm.FirmName = request.FirmName;
                    context.SaveChanges();
                }
            }
        }
    }
}
