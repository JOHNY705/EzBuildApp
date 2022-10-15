using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServicesMessaging.EmployeeHours;
using EzBuildServicesMessaging.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;

namespace EzBuildDataAccess.Repository.Implementations
{
    class EmployeeHoursRepository : IEmployeeHoursRepository
    {
        private readonly EzBuildDBEntities _context = new EzBuildDBEntities();

        public void CreateEmployeeHoursForDay(CreateEmployeeHoursForDayRequest request)
        {
            using (var context = _context)
            {
                var employeeHours = new List<EmployeeHour>();

                foreach (var eh in request.EmployeeHours)
                {
                    var eHours = context.EmployeeHours.FirstOrDefault(eHr => eHr.EmployeeID == eh.EmployeeID && eHr.DateWorkDone == eh.DateWorkDone);

                    if (eHours != null)
                    {
                        eHours.HoursWorked = eh.HoursWorked;
                    }
                    else
                    {
                        context.EmployeeHours.Add(new EmployeeHour
                        {
                            HoursWorked = eh.HoursWorked,
                            DateWorkDone = eh.DateWorkDone,
                            EmployeeID = eh.EmployeeID,
                        });
                    }
                }

                context.SaveChanges();
            }
        }

        public IEnumerable<EmployeeHoursMonthVM> GetEmployeeHoursMonthForEmployee(int employeeID)
        {
            using (var context = _context)
            {

                //var ehGroup = from eh in context.EmployeeHours
                //           where eh.EmployeeID == employeeID
                //           group eh by new
                //           {
                //               eh.EmployeeID,
                //               eh.DateWorkDone.Year,
                //               eh.DateWorkDone.Month,
                //           } into ehmGroup
                //           orderby ehmGroup.Key.Month descending
                //           orderby ehmGroup.Key.Year descending
                //           select new
                //           {
                //               HoursWorked = (short)ehmGroup.Sum(e => e.HoursWorked),
                //               YearWorkDone = ehmGroup.Key.Year,
                //               MonthWorkDone = ehmGroup.Key.Month,
                //               EmployeeID = ehmGroup.Key.EmployeeID,
                //           };

                var ehGroup = context.EmployeeHours
                    .Where(eh => eh.EmployeeID == employeeID)
                    .GroupBy(eh => new { eh.EmployeeID, eh.DateWorkDone.Year, eh.DateWorkDone.Month })
                    .Select(eh => new {
                        HoursWorked = (short)eh.Sum(e => e.HoursWorked),
                        YearWorkDone = eh.Key.Year,
                        MonthWorkDone = eh.Key.Month,
                        EmployeeID = eh.Key.EmployeeID,
                    });

                var ehmVM = new List<EmployeeHoursMonthVM>();

                foreach (var item in ehGroup)
                {
                    ehmVM.Add(new EmployeeHoursMonthVM(item.HoursWorked, new DateTime(item.YearWorkDone, item.MonthWorkDone, 1), item.EmployeeID));
                }

                return ehmVM;
            }
        }

        public IEnumerable<EmployeeHoursVM> GetEmployeeHoursForFirm(int firmID)
        {
            using (var context = _context)
            {
                var employeeHours = context.EmployeeHours.Where(eh => eh.Employee.FirmID == firmID);
                var employeeHoursVM = new List<EmployeeHoursVM>();

                if (employeeHours != null)
                {
                    foreach (var eh in employeeHours)
                    {
                        employeeHoursVM.Add(new EmployeeHoursVM
                        {
                            HoursWorked = eh.HoursWorked,
                            DateWorkDone = eh.DateWorkDone,
                            EmployeeFullName = eh.Employee.FullName,
                            EmployeeID = eh.EmployeeID,
                        });
                    }
                }

                return employeeHoursVM;
            }
        }
        public IEnumerable<EmployeeHoursVM> GetEmployeeHoursForCS(int constructionSiteID)
        {
            using (var context = _context)
            {
                var employeeHours = context.EmployeeHours.Where(eh => eh.Employee.ConstructionSiteID == constructionSiteID && eh.Employee.EmployeeTypeID == 4);
                var employeeHoursVM = new List<EmployeeHoursVM>();

                if (employeeHours != null)
                {
                    foreach (var eh in employeeHours)
                    {
                        employeeHoursVM.Add(new EmployeeHoursVM
                        {
                            IDEmployeeHours = eh.IDEmployeeHours,
                            HoursWorked = eh.HoursWorked,
                            DateWorkDone = eh.DateWorkDone,
                            EmployeeFullName = eh.Employee.FullName,
                            EmployeeID = eh.EmployeeID,
                        });
                    }
                }

                return employeeHoursVM;
            }
        }

        public IEnumerable<EmployeeHoursVM> GetEmployeeHoursForEngineers(int firmID)
        {
            using (var context = _context)
            {
                var employeeHours = context.EmployeeHours.Where(eh => eh.Employee.FirmID == firmID && eh.Employee.EmployeeTypeID == 2);
                var employeeHoursVM = new List<EmployeeHoursVM>();

                if (employeeHours != null)
                {
                    foreach (var eh in employeeHours)
                    {
                        employeeHoursVM.Add(new EmployeeHoursVM
                        {
                            IDEmployeeHours = eh.IDEmployeeHours,
                            HoursWorked = eh.HoursWorked,
                            DateWorkDone = eh.DateWorkDone,
                            EmployeeFullName = eh.Employee.FullName,
                            EmployeeID = eh.EmployeeID,
                        });
                    }
                }

                return employeeHoursVM;
            }
        }

        public IEnumerable<EmployeeHoursVM> GetEmployeeHoursForPhysicalWorkers(int firmID)
        {
            using (var context = _context)
            {
                var employeeHours = context.EmployeeHours.Where(eh => eh.Employee.FirmID == firmID && eh.Employee.EmployeeTypeID == 4);
                var employeeHoursVM = new List<EmployeeHoursVM>();

                if (employeeHours != null)
                {
                    foreach (var eh in employeeHours)
                    {
                        employeeHoursVM.Add(new EmployeeHoursVM
                        {
                            IDEmployeeHours = eh.IDEmployeeHours,
                            HoursWorked = eh.HoursWorked,
                            DateWorkDone = eh.DateWorkDone,
                            EmployeeFullName = eh.Employee.FullName,
                            EmployeeID = eh.EmployeeID,
                        });
                    }
                }

                return employeeHoursVM;
            }
        }

        public IEnumerable<EmployeeHoursVM> GetEmployeeHoursForWarehouseManager(int firmID)
        {
            using (var context = _context)
            {
                var employeeHours = context.EmployeeHours.Where(eh => eh.Employee.FirmID == firmID && eh.Employee.EmployeeTypeID == 3);
                var employeeHoursVM = new List<EmployeeHoursVM>();

                if (employeeHours != null)
                {
                    foreach (var eh in employeeHours)
                    {
                        employeeHoursVM.Add(new EmployeeHoursVM
                        {
                            IDEmployeeHours = eh.IDEmployeeHours,
                            HoursWorked = eh.HoursWorked,
                            DateWorkDone = eh.DateWorkDone,
                            EmployeeFullName = eh.Employee.FullName,
                            EmployeeID = eh.EmployeeID,
                        });
                    }
                }

                return employeeHoursVM;
            }
        }
    }
}
