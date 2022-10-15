using EzBuildServicesMessaging.Firm;

namespace EzBuildServices.Interfaces
{
    public interface IFirmService
    {
        GetFirmNameResponse GetFirmName(int firmID);
        RegisterFirmAndUserResponse RegisterFirmAndUser(RegisterFirmAndUserRequest request);
        UpdateFirmNameResponse UpdateFirmName(UpdateFirmNameRequest request);
    }
}
