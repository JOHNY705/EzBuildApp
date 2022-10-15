using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServicesMessaging.ConstructionSites;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;
using System.Linq;

namespace EzBuildDataAccess.Repository.Implementations
{
    class ConstructionSiteRepository : IConstructionSiteRepository
    {
        private readonly EzBuildDBEntities _context = new EzBuildDBEntities();

        public IEnumerable<ConstructionSiteVM> GetConstructionSitesForFirm(int firmID)
        {
            using (var context = _context)
            {
                var constructionSites = context.ConstructionSites.Where(cs => cs.FirmID == firmID).ToList();
                var constructionSitesVM = new List<ConstructionSiteVM>();
                foreach (var cs in constructionSites)
                {
                    var csManager = cs.Employee;
                    var employeeVM = new EmployeeVM(
                        csManager.IDEmployee, csManager.FirebaseID, 
                        csManager.FullName, csManager.Email, csManager.Phone, csManager.FirmID,
                        csManager.EmployeeTypeID, csManager.ConstructionSiteID
                        );

                    var employees = cs.Employees.Where(e => e.ConstructionSiteID == cs.IDConstructionSite && e.IDEmployee != cs.ConstructionSiteManagerID).ToList();
                    var employeesVM = new List<EmployeeVM>();
                    foreach (var employee in employees)
                    {
                        employeesVM.Add(new EmployeeVM(
                            employee.IDEmployee, employee.FirebaseID,
                            employee.FullName, employee.Email, csManager.Phone, employee.FirmID,
                            employee.EmployeeTypeID, employee.ConstructionSiteID
                        ));
                    }

                    var diaryEntries = cs.ConstructionSiteDiaryEntries.Where(de => de.ConstructionSiteID == cs.IDConstructionSite);
                    var diaryEntriesVM = new List<ConstructionSiteDiaryEntryVM>();
                    foreach (var diaryEntry in diaryEntries)
                    {
                        diaryEntriesVM.Add(new ConstructionSiteDiaryEntryVM
                        {
                            IDConstructionSiteDiary = diaryEntry.IDConstructionSiteDiary,
                            DiaryEntry = diaryEntry.DiaryEntry,
                            DiaryEntryDate = diaryEntry.DiaryEntryDate,
                            EmployeeFullName = diaryEntry.Employee.FullName,
                            ConstructionSiteID = diaryEntry.ConstructionSiteID
                        });
                    }

                    constructionSitesVM.Add(new ConstructionSiteVM
                    {
                        IDConstructionSite = cs.IDConstructionSite,
                        Base64Image = cs.Base64Image,
                        FullAddress = cs.FullAddress,
                        Latitude = cs.Latitude,
                        Longitude = cs.Longitude,
                        IsActive = cs.IsActive,
                        ConstructionSiteManager = employeeVM,
                        ConstructionSiteDiaryEntries = diaryEntriesVM,
                        Employees = employeesVM,
                    });
                }

                return constructionSitesVM;
            }
        }

        public IEnumerable<ConstructionSiteVM> GetConstructionSitesForFirmNoImage(int firmID)
        {
            using (var context = _context)
            {
                var constructionSites = context.ConstructionSites.Where(cs => cs.FirmID == firmID).ToList();
                var constructionSitesVM = new List<ConstructionSiteVM>();
                foreach (var cs in constructionSites)
                {
                    var csManager = cs.Employee;
                    var employeeVM = new EmployeeVM(
                        csManager.IDEmployee, csManager.FirebaseID,
                        csManager.FullName, csManager.Email, csManager.Phone, csManager.FirmID,
                        csManager.EmployeeTypeID, csManager.ConstructionSiteID
                        );

                    var employees = cs.Employees.Where(e => e.ConstructionSiteID == cs.IDConstructionSite && e.IDEmployee != cs.ConstructionSiteManagerID).ToList();
                    var employeesVM = new List<EmployeeVM>();
                    foreach (var employee in employees)
                    {
                        employeesVM.Add(new EmployeeVM(
                            employee.IDEmployee, employee.FirebaseID,
                            employee.FullName, employee.Email, csManager.Phone, employee.FirmID,
                            employee.EmployeeTypeID, employee.ConstructionSiteID
                        ));
                    }

                    var diaryEntries = cs.ConstructionSiteDiaryEntries.Where(de => de.ConstructionSiteID == cs.IDConstructionSite);
                    var diaryEntriesVM = new List<ConstructionSiteDiaryEntryVM>();
                    foreach (var diaryEntry in diaryEntries)
                    {
                        diaryEntriesVM.Add(new ConstructionSiteDiaryEntryVM
                        {
                            IDConstructionSiteDiary = diaryEntry.IDConstructionSiteDiary,
                            DiaryEntry = diaryEntry.DiaryEntry,
                            DiaryEntryDate = diaryEntry.DiaryEntryDate,
                            EmployeeFullName = diaryEntry.Employee.FullName,
                            ConstructionSiteID = diaryEntry.ConstructionSiteID
                        });
                    }

                    constructionSitesVM.Add(new ConstructionSiteVM
                    {
                        IDConstructionSite = cs.IDConstructionSite,
                        Base64Image = "",
                        FullAddress = cs.FullAddress,
                        Latitude = cs.Latitude,
                        Longitude = cs.Longitude,
                        IsActive = cs.IsActive,
                        ConstructionSiteManager = employeeVM,
                        ConstructionSiteDiaryEntries = diaryEntriesVM,
                        Employees = employeesVM,
                    });
                }

                return constructionSitesVM;
            }
        }

        public IEnumerable<ConstructionSiteVM> GetConstructionSitesForEmployee(int employeeID)
        {
            using (var context = _context)
            {
                var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == employeeID);

                var constructionSitesVM = new List<ConstructionSiteVM>();

                if (employee != null)
                {
                    if (employee.EmployeeTypeID == 2)
                    {
                        var constructionSites = context.ConstructionSites.Where(cs => cs.ConstructionSiteManagerID == employeeID).ToList();
                        foreach (var cs in constructionSites)
                        {
                            var csManager = cs.Employee;
                            var employeeVM = new EmployeeVM(
                                csManager.IDEmployee, csManager.FirebaseID,
                                csManager.FullName, csManager.Email, csManager.Phone, csManager.FirmID,
                                csManager.EmployeeTypeID, csManager.ConstructionSiteID
                                );

                            var employees = cs.Employees.Where(e => e.ConstructionSiteID == cs.IDConstructionSite && e.IDEmployee != cs.ConstructionSiteManagerID).ToList();
                            var employeesVM = new List<EmployeeVM>();
                            foreach (var e in employees)
                            {
                                employeesVM.Add(new EmployeeVM(
                                    employee.IDEmployee, employee.FirebaseID,
                                    employee.FullName, employee.Email, csManager.Phone, employee.FirmID,
                                    employee.EmployeeTypeID, employee.ConstructionSiteID
                                ));
                            }

                            var diaryEntries = cs.ConstructionSiteDiaryEntries.Where(de => de.ConstructionSiteID == cs.IDConstructionSite);
                            var diaryEntriesVM = new List<ConstructionSiteDiaryEntryVM>();
                            foreach (var diaryEntry in diaryEntries)
                            {
                                diaryEntriesVM.Add(new ConstructionSiteDiaryEntryVM
                                {
                                    IDConstructionSiteDiary = diaryEntry.IDConstructionSiteDiary,
                                    DiaryEntry = diaryEntry.DiaryEntry,
                                    DiaryEntryDate = diaryEntry.DiaryEntryDate,
                                    EmployeeFullName = diaryEntry.Employee.FullName,
                                    ConstructionSiteID = diaryEntry.ConstructionSiteID
                                });
                            }

                            constructionSitesVM.Add(new ConstructionSiteVM
                            {
                                IDConstructionSite = cs.IDConstructionSite,
                                Base64Image = cs.Base64Image,
                                FullAddress = cs.FullAddress,
                                Latitude = cs.Latitude,
                                Longitude = cs.Longitude,
                                IsActive = cs.IsActive,
                                ConstructionSiteManager = employeeVM,
                                ConstructionSiteDiaryEntries = diaryEntriesVM,
                                Employees = employeesVM,
                            });
                        }
                    }
                    else if (employee.EmployeeTypeID == 4)
                    {
                        var constructionSites = context.ConstructionSites.Where(cs => cs.IDConstructionSite == employee.ConstructionSiteID).ToList();
                        foreach (var cs in constructionSites)
                        {
                            var csManager = cs.Employee;
                            var employeeVM = new EmployeeVM(
                                csManager.IDEmployee, csManager.FirebaseID,
                                csManager.FullName, csManager.Email, csManager.Phone, csManager.FirmID,
                                csManager.EmployeeTypeID, csManager.ConstructionSiteID
                                );

                            var employees = cs.Employees.Where(e => e.ConstructionSiteID == cs.IDConstructionSite && e.IDEmployee != cs.ConstructionSiteManagerID).ToList();
                            var employeesVM = new List<EmployeeVM>();
                            foreach (var e in employees)
                            {
                                employeesVM.Add(new EmployeeVM(
                                    employee.IDEmployee, employee.FirebaseID,
                                    employee.FullName, employee.Email, csManager.Phone, employee.FirmID,
                                    employee.EmployeeTypeID, employee.ConstructionSiteID
                                ));
                            }

                            var diaryEntries = cs.ConstructionSiteDiaryEntries.Where(de => de.ConstructionSiteID == cs.IDConstructionSite);
                            var diaryEntriesVM = new List<ConstructionSiteDiaryEntryVM>();
                            foreach (var diaryEntry in diaryEntries)
                            {
                                diaryEntriesVM.Add(new ConstructionSiteDiaryEntryVM
                                {
                                    IDConstructionSiteDiary = diaryEntry.IDConstructionSiteDiary,
                                    DiaryEntry = diaryEntry.DiaryEntry,
                                    DiaryEntryDate = diaryEntry.DiaryEntryDate,
                                    EmployeeFullName = diaryEntry.Employee.FullName,
                                    ConstructionSiteID = diaryEntry.ConstructionSiteID
                                });
                            }

                            constructionSitesVM.Add(new ConstructionSiteVM
                            {
                                IDConstructionSite = cs.IDConstructionSite,
                                Base64Image = cs.Base64Image,
                                FullAddress = cs.FullAddress,
                                Latitude = cs.Latitude,
                                Longitude = cs.Longitude,
                                IsActive = cs.IsActive,
                                ConstructionSiteManager = employeeVM,
                                ConstructionSiteDiaryEntries = diaryEntriesVM,
                                Employees = employeesVM,
                            });
                        }
                    }
                }

                return constructionSitesVM;
            }
        }

        public ConstructionSiteVM GetConstructionSite(int constructionSiteID)
        {
            using (var context = _context)
            {
                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == constructionSiteID);
                ConstructionSiteVM constructionSiteVM = new ConstructionSiteVM();
                if (constructionSite != null)
                {
                    constructionSiteVM.IDConstructionSite = constructionSite.IDConstructionSite;
                    constructionSiteVM.Base64Image = constructionSite.Base64Image;
                    constructionSiteVM.FullAddress = constructionSite.FullAddress;
                    constructionSiteVM.Latitude = constructionSite.Latitude;
                    constructionSiteVM.Longitude = constructionSite.Longitude;
                    constructionSiteVM.IsActive = constructionSite.IsActive;

                    var csManager = constructionSite.Employee;
                    constructionSiteVM.ConstructionSiteManager = new EmployeeVM(
                        csManager.IDEmployee, csManager.FirebaseID,
                        csManager.FullName, csManager.Email, csManager.Phone, csManager.FirmID,
                        csManager.EmployeeTypeID, csManager.ConstructionSiteID
                        );

                    var employees = constructionSite.Employees.Where(e => e.ConstructionSiteID == constructionSite.IDConstructionSite 
                        && e.IDEmployee != constructionSite.ConstructionSiteManagerID).ToList();
                    var employeesVM = new List<EmployeeVM>();
                    foreach (var employee in employees)
                    {
                        employeesVM.Add(new EmployeeVM(
                            employee.IDEmployee, employee.FirebaseID,
                            employee.FullName, employee.Email, csManager.Phone, employee.FirmID,
                            employee.EmployeeTypeID, employee.ConstructionSiteID
                        ));
                    }

                    constructionSiteVM.Employees = employeesVM;

                    var diaryEntries = constructionSite.ConstructionSiteDiaryEntries.Where(de => de.ConstructionSiteID == constructionSite.IDConstructionSite);
                    var diaryEntriesVM = new List<ConstructionSiteDiaryEntryVM>();
                    foreach (var diaryEntry in diaryEntries)
                    {
                        diaryEntriesVM.Add(new ConstructionSiteDiaryEntryVM
                        {
                            IDConstructionSiteDiary = diaryEntry.IDConstructionSiteDiary,
                            DiaryEntry = diaryEntry.DiaryEntry,
                            DiaryEntryDate = diaryEntry.DiaryEntryDate,
                            EmployeeFullName = diaryEntry.Employee.FullName,
                            ConstructionSiteID = diaryEntry.ConstructionSiteID
                        });
                    }

                    constructionSiteVM.ConstructionSiteDiaryEntries = diaryEntriesVM;
                }

                return constructionSiteVM;
            }
        }


        public ConstructionSiteVM UpdateConstructionSite(UpdateConstructionSiteRequest request)
        {
            using (var context = _context)
            {
                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == request.IDConstructionSite);
                if (constructionSite != null)
                {
                    constructionSite.Base64Image = request.Base64Image;
                    constructionSite.FullAddress = request.FullAddress;
                    constructionSite.Latitude = request.Latitude;
                    constructionSite.Longitude = request.Longitude;
                    constructionSite.IsActive = request.IsActive;
                    constructionSite.ConstructionSiteManagerID = request.ConstructionSiteManagerID;

                    constructionSite.Employees.Clear();

                    context.SaveChanges();

                    if (request.Employees.Count() > 0)
                    {
                        foreach (var id in request.Employees)
                        {
                            var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == id);
                            if (employee != null)
                            {
                                employee.ConstructionSiteID = request.IDConstructionSite;
                            }
                        }
                    }

                    context.SaveChanges();
                }

                return GetConstructionSite(request.IDConstructionSite);
            }
        }

        public void UpdateConstructionSiteImage(UpdateConstructionSiteImageRequest request)
        {
            using (var context = _context)
            {
                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == request.IDConstructionSite);
                if (constructionSite != null)
                {
                    constructionSite.Base64Image = request.Base64Image;
                    context.SaveChanges();
                }
            }
        }

        public void CreateConstructionSite(CreateConstructionSiteRequest request)
        {
            using (var context = _context)
            {
                ConstructionSite constructionSite = new ConstructionSite
                {
                    Base64Image = request.Base64Image,
                    FullAddress = request.FullAddress,
                    Latitude = request.Latitude,
                    Longitude = request.Longitude,
                    IsActive = request.IsActive,
                    FirmID = request.FirmID,
                    ConstructionSiteManagerID = request.ConstructionSiteManagerID,
                };

                context.ConstructionSites.Add(constructionSite);
                context.SaveChanges();

                int constructionSiteID = constructionSite.IDConstructionSite;

                //var manager = context.Employees.FirstOrDefault(e => e.IDEmployee == request.ConstructionSiteManagerID);
                //if (manager != null)
                //{
                //    manager.ConstructionSiteID = constructionSiteID;
                //}

                if (request.Employees.Count() > 0)
                {
                    foreach (var id in request.Employees)
                    {
                        var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == id);
                        if (employee != null)
                        {
                            employee.ConstructionSiteID = constructionSiteID;
                        }
                    }
                }

                context.SaveChanges();
            }
        }

        public void RemoveEmployeeFromConstructionSite(RemoveEmployeeFromConstructionSiteRequest request)
        {
            using (var context = _context)
            {
                var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == request.IDEmployee);
                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == request.IDConstructionSite);
                if (employee != null && constructionSite != null)
                {
                    if (employee.EmployeeTypeID == 2)
                    {
                        constructionSite.ConstructionSiteManagerID = null;
                    }

                    employee.ConstructionSiteID = null;
                    context.SaveChanges();
                }
            }
        }

        public void UpdateCSManagerForConstructionSite(UpdateCSManagerRequest request)
        {
            using (var context = _context)
            {
                var employee = context.Employees.FirstOrDefault(e => e.IDEmployee == request.IDEmployee);
                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == request.IDConstructionSite);
                if (employee != null && constructionSite != null)
                {
                    if (employee.EmployeeTypeID == 2)
                    {
                        constructionSite.ConstructionSiteManagerID = request.IDEmployee;

                        context.SaveChanges();
                    }
                }
            }
        }

        public void DeleteConstructionSite(int constructionSiteID)
        {
            using (var context = _context)
            {
                var employees = context.Employees.Where(e => e.ConstructionSiteID == constructionSiteID);
                var diaryEntries = context.ConstructionSiteDiaryEntries.Where(de => de.ConstructionSiteID == constructionSiteID);

                if (employees != null)
                {
                    foreach (var employee in employees)
                    {
                        employee.ConstructionSiteID = null;
                    }
                }

                if (diaryEntries != null)
                {
                    context.ConstructionSiteDiaryEntries.RemoveRange(diaryEntries);
                }

                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == constructionSiteID);
                if (constructionSite != null)
                {
                    context.ConstructionSites.Remove(constructionSite);
                    context.SaveChanges();
                }
            }
        }

        public void CreateCSDiaryEntry(CreateCSDiaryEntryRequest request)
        {
            using (var context = _context)
            {
                var constructionSite = context.ConstructionSites.FirstOrDefault(cs => cs.IDConstructionSite == request.ConstructionSiteID);

                if (constructionSite != null)
                {
                    ConstructionSiteDiaryEntry diaryEntry = new ConstructionSiteDiaryEntry
                    {
                        ConstructionSiteID = request.ConstructionSiteID,
                        DiaryEntry = request.DiaryEntry,
                        DiaryEntryDate = request.DiaryEntryDate,
                        DiaryEntryEmployeeID = request.DiaryEntryEmployeeID,
                    };

                    context.ConstructionSiteDiaryEntries.Add(diaryEntry);
                    context.SaveChanges();
                }
            }
        }

        public void DeleteCSDiaryEntry(int diaryEntryID)
        {
            using (var context = _context)
            {
                var diaryEntry = context.ConstructionSiteDiaryEntries.FirstOrDefault(csde => csde.IDConstructionSiteDiary == diaryEntryID);

                if (diaryEntry != null)
                {
                    context.ConstructionSiteDiaryEntries.Remove(diaryEntry);
                    context.SaveChanges();
                }
            }
        }

        public void UpdateCSDiaryEntry(UpdateCSDiaryEntryRequest request)
        {
            using (var context = _context)
            {
                var diaryEntry = context.ConstructionSiteDiaryEntries.FirstOrDefault(csde => csde.IDConstructionSiteDiary == request.IDConstructionSiteDiaryEntry);

                if (diaryEntry != null)
                {
                    diaryEntry.DiaryEntry = request.DiaryEntry;
                    diaryEntry.DiaryEntryDate = request.DiaryEntryDate;
                    diaryEntry.DiaryEntryEmployeeID = request.DiaryEntryEmployeeID;

                    context.SaveChanges();
                }
            }
        }
    }
}
