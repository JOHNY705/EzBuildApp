using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServicesMessaging.Employees;
using System.Collections.Generic;
using System.Linq;

namespace EzBuildDataAccess.Repository.Implementations
{
    class EmployeeRepository : IEmployeeRepository
    {
        private readonly EzBuildDBEntities _context = new EzBuildDBEntities();

        public IEnumerable<Employee> GetEmployeesForFirm(int firmID)
        {
            using (var context = _context)
            {
                return context.Employees.Where(e => e.FirmID == firmID && e.EmployeeTypeID != 1).ToList();
            }
        }

        public IEnumerable<Employee> GetEmployeesForConstructionSite(int constructionSiteID)
        {
            using (var context = _context)
            {
                return context.Employees.Where(e => e.ConstructionSiteID == constructionSiteID).ToList();
            }
        }

        public IEnumerable<Employee> GetEngineersForFirm(int firmID)
        {
            using (var context = _context)
            {
                return context.Employees.Where(e => e.FirmID == firmID && e.EmployeeTypeID == 2).ToList();
            }
        }

        public IEnumerable<Employee> GetPhysicalWorkersForFirm(int firmID)
        {
            using (var context = _context)
            {
                return context.Employees.Where(e => e.FirmID == firmID && e.EmployeeTypeID == 4).ToList();
            }
        }

        public IEnumerable<Employee> GetWarehouseManagerForFirm(int firmID)
        {
            using (var context = _context)
            {
                return context.Employees.Where(e => e.FirmID == firmID && e.EmployeeTypeID == 3).ToList();
            }
        }

        public IEnumerable<Employee> GetUnassignedEmployeesForFirm(int firmID)
        {
            using (var context = _context)
            {
                return context.Employees.Where(e => e.FirmID == firmID && e.EmployeeTypeID == 4 && e.ConstructionSiteID == null).ToList();
            }
        }

        public void CreateEmployee(CreateEmployeeRequest request)
        {
            using (var context = _context)
            {
                Employee employee = new Employee
                {
                    FirebaseID = request.FirebaseID,
                    FullName = request.FullName,
                    Email = request.Email,
                    Phone = request.Phone,
                    EmployeeTypeID = request.EmployeeTypeID,
                    FirmID = request.FirmID,
                };

                context.Employees.Add(employee);
                context.SaveChanges();
            }           
        }

        public Employee GetRegisteredEmployeeDetails(string firebaseUID)
        {
            using (var context = _context)
            {
                return context.Employees.FirstOrDefault(e => e.FirebaseID == firebaseUID);
            }
        }

        public void UpdateEmployee(UpdateEmployeeRequest request)
        {
            using (var context = _context)
            {
                var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == request.IDEmployee);
                if (employee != null)
                {
                    employee.FullName = request.FullName;
                    employee.Phone = request.Phone;

                    context.SaveChanges();
                }
            }
        }

        public void DeleteEmployee(int employeeID)
        {
            using (var context = _context)
            {
                var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == employeeID);
                if (employee != null)
                {
                    var employeeHours = context.EmployeeHours.Where(eh => eh.EmployeeID == employee.IDEmployee);
                    context.EmployeeHours.RemoveRange(employeeHours);

                    if (employee.EmployeeTypeID == 2)
                    {
                        var director = context.Employees.FirstOrDefault(d => d.FirmID == employee.FirmID);
                        var constructionSiteDiaryEntries = context.ConstructionSiteDiaryEntries.Where(csde => csde.DiaryEntryEmployeeID == employee.IDEmployee);
                        if (director != null)
                        {
                            foreach (var csde in constructionSiteDiaryEntries)
                            {
                                csde.DiaryEntryEmployeeID = director.IDEmployee;
                            }
                        }
                    } 
                    else if (employee.EmployeeTypeID == 4)
                    {
                        var equipmentHistory = context.EquipmentHistories.Where(eh => eh.EmployeeID == employee.IDEmployee);
                        if (equipmentHistory != null)
                        {
                            foreach (var eq in equipmentHistory)
                            {
                                var equipment = context.Equipments.FirstOrDefault(e => e.IDEquipment == eq.EquipmentID);
                                if (equipment != null)
                                {
                                    equipment.Quantity += eq.QuantityTaken;
                                }
                            }

                            context.EquipmentHistories.RemoveRange(equipmentHistory);
                            context.SaveChanges();
                        }
                    }
                    var meetings = context.Meetings.Where(m => m.EmployeeID == employee.IDEmployee);
                    context.Meetings.RemoveRange(meetings);

                    context.Employees.Remove(employee);
                    context.SaveChanges();
                }
            }
        }
    }
}
