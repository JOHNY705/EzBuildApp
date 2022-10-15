namespace EzBuildServicesMessaging.ViewModels
{
    public class WarehouseVM
    {
        public int IDWarehouse { get; set; }
        public string FullAddress { get; set; }
        public EmployeeVM WarehouseManager { get; set; }
    }
}
