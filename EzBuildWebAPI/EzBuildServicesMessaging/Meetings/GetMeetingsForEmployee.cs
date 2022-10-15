using EzBuildServicesMessaging.Base;
using EzBuildServicesMessaging.ViewModels;
using System.Collections.Generic;

namespace EzBuildServicesMessaging.Meetings
{
    public class GetMeetingsForEmployeeResponse : BaseResponse
    {
        public IEnumerable<MeetingVM> Meetings { get; set; }
    }
}
