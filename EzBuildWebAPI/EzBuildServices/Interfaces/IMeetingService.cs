using EzBuildServicesMessaging.Meetings;

namespace EzBuildServices.Interfaces
{
    public interface IMeetingService
    {
        CreateMeetingResponse CreateMeeting(CreateMeetingRequest request);
        GetMeetingsForEmployeeResponse GetMeetingsForEmployee(int employeeID);
        UpdateMeetingResponse UpdateMeeting(UpdateMeetingRequest request);
        DeleteMeetingResponse DeleteMeeting(int employeeID);
    }
}
