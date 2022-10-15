using EzBuildDataAccess;
using EzBuildDataAccess.Repository.Factory;
using EzBuildDataAccess.Repository.Interfaces;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Meetings;
using EzBuildServicesMessaging.ViewModels;
using System;
using System.Collections.Generic;

namespace EzBuildServices.Implementations
{
    class MeetingService : IMeetingService
    {
        private readonly IMeetingRepository meetingRepository = RepositoryFactory.GetMeetingRepository();
        public CreateMeetingResponse CreateMeeting(CreateMeetingRequest request)
        {
            CreateMeetingResponse response = new CreateMeetingResponse();

            try
            {
                meetingRepository.CreateMeeting(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public GetMeetingsForEmployeeResponse GetMeetingsForEmployee(int employeeID)
        {
            GetMeetingsForEmployeeResponse response = new GetMeetingsForEmployeeResponse();

            try
            {
                IEnumerable<Meeting> meetings = meetingRepository.GetMeetingsForEmployee(employeeID);

                ICollection<MeetingVM> meetingsVMList = new List<MeetingVM>();
                foreach (Meeting m in meetings)
                {
                    meetingsVMList.Add(new MeetingVM(m.IDMeeting, m.Title, m.MeetingDate, m.MeetingStartTime, m.MeetingDuration, m.MeetingDescription));
                }

                response.Meetings = meetingsVMList;
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public UpdateMeetingResponse UpdateMeeting(UpdateMeetingRequest request)
        {
            UpdateMeetingResponse response = new UpdateMeetingResponse();

            try
            {
                meetingRepository.UpdateMeeting(request);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }

        public DeleteMeetingResponse DeleteMeeting(int meetingID)
        {
            DeleteMeetingResponse response = new DeleteMeetingResponse();

            try
            {
                meetingRepository.DeleteMeeting(meetingID);
            }
            catch (Exception ex)
            {
                response.IsSuccessful = false;
                response.Message = ex.Message;
            }

            return response;
        }
    }
}
