using EzBuildServicesMessaging.Base;
using System;

namespace EzBuildServicesMessaging.Meetings
{
    public class UpdateMeetingRequest : BaseRequest
    {
        public int IDMeeting { get; set; }
        public string Title { get; set; }
        public DateTime MeetingDate { get; set; }
        public TimeSpan MeetingStartTime { get; set; }
        public string MeetingDuration { get; set; }
        public string MeetingDescription { get; set; }
    }

    public class UpdateMeetingResponse : BaseResponse
    {
    }
}
