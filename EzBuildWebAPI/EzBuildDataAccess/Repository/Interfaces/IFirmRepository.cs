using EzBuildServicesMessaging.Firm;

namespace EzBuildDataAccess.Repository.Interfaces
{
    public interface IFirmRepository
    {
        string GetFirmName(int firmID);
        Employee RegisterFirmAndUser(RegisterFirmAndUserRequest request);
        void UpdateFirmName(UpdateFirmNameRequest request);
    }
}
