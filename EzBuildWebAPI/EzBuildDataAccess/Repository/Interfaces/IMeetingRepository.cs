using EzBuildServicesMessaging.Meetings;
using System.Collections.Generic;

namespace EzBuildDataAccess.Repository.Interfaces
{
    public interface IMeetingRepository
    {
        void CreateMeeting(CreateMeetingRequest request);
        IEnumerable<Meeting> GetMeetingsForEmployee(int employeeID);
        void UpdateMeeting(UpdateMeetingRequest request);
        void DeleteMeeting(int meetingID);
    }
}
