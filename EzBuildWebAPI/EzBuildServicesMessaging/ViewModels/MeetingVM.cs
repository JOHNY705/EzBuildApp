using System;

namespace EzBuildServicesMessaging.ViewModels
{
    public class MeetingVM
    {
        public MeetingVM(int iDMeeting, string title, DateTime meetingDate, TimeSpan meetingStartTime, string meetingDuration, string meetingDescription)
        {
            IDMeeting = iDMeeting;
            Title = title;
            MeetingDate = meetingDate;
            MeetingStartTime = meetingStartTime;
            MeetingDuration = meetingDuration;
            MeetingDescription = meetingDescription;
        }

        public int IDMeeting { get; set; }
        public string Title { get; set; }
        public DateTime MeetingDate { get; set; }
        public TimeSpan MeetingStartTime { get; set; }
        public string MeetingDuration { get; set; }
        public string MeetingDescription { get; set; }
    }
}
