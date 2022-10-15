using EzBuildServicesMessaging.Base;
using System;

namespace EzBuildServicesMessaging.Meetings
{
    public class CreateMeetingRequest : BaseRequest
    {
        public string Title { get; set; }
        public DateTime MeetingDate { get; set; }
        public TimeSpan MeetingStartTime { get; set; }
        public string MeetingDuration { get; set; }
        public string MeetingDescription { get; set; }
        public int EmployeeID { get; set; }
    }

    public class CreateMeetingResponse : BaseResponse
    {
    }
}
