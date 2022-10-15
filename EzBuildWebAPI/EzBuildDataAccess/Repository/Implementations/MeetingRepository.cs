using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServicesMessaging.Meetings;
using System.Collections.Generic;
using System.Linq;

namespace EzBuildDataAccess.Repository.Implementations
{
    class MeetingRepository : IMeetingRepository
    {
        private readonly EzBuildDBEntities _context = new EzBuildDBEntities();
        public void CreateMeeting(CreateMeetingRequest request)
        {
            using (var context = _context)
            {
                Meeting meeting = new Meeting
                {
                    Title = request.Title,
                    MeetingDate = request.MeetingDate,
                    MeetingStartTime = request.MeetingStartTime,
                    MeetingDuration = request.MeetingDuration,
                    MeetingDescription = request.MeetingDescription,
                    EmployeeID = request.EmployeeID,
                };

                context.Meetings.Add(meeting);
                context.SaveChanges();
            }
        }

        public IEnumerable<Meeting> GetMeetingsForEmployee(int employeeID)
        {
            using (var context = _context)
            {
                return context.Meetings.Where(m => m.EmployeeID == employeeID).ToList();
            }
        }

        public void UpdateMeeting(UpdateMeetingRequest request)
        {
            using (var context = _context)
            {
                var meeting = context.Meetings.FirstOrDefault(m => m.IDMeeting == request.IDMeeting);
                if (meeting != null)
                {
                    meeting.Title = request.Title;
                    meeting.MeetingDate = request.MeetingDate;
                    meeting.MeetingStartTime = request.MeetingStartTime;
                    meeting.MeetingDuration = request.MeetingDuration;
                    meeting.MeetingDescription = request.MeetingDescription;

                    context.SaveChanges();
                }
            }
        }

        public void DeleteMeeting(int meetingID)
        {
            using (var context = _context)
            {
                var meeting = context.Meetings.FirstOrDefault(m => m.IDMeeting == meetingID);
                if (meeting != null)
                {
                    context.Meetings.Remove(meeting);
                    context.SaveChanges();
                }
            }
        }
    }
}
