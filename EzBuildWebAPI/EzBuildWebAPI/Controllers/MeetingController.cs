using EzBuildServices.Factory;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Meetings;
using System.Web.Http;

namespace EzBuildWebAPI.Controllers
{
    [RoutePrefix("api")]
    public class MeetingController : ApiController
    {
        private readonly IMeetingService meetingService = ObjectFactory.GetMeetingService();

        [HttpGet]
        [Route("meetings/{employeeID}")]
        public IHttpActionResult GetMeetingsForEmployee(int employeeID)
        {
            var response = meetingService.GetMeetingsForEmployee(employeeID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("meetings")]
        public IHttpActionResult CreateMeeting([FromBody] CreateMeetingRequest request)
        {
            var response = meetingService.CreateMeeting(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("meetings")]
        public IHttpActionResult UpdateMeeting([FromBody] UpdateMeetingRequest request)
        {
            var response = meetingService.UpdateMeeting(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpDelete]
        [Route("meetings/{meetingID}")]
        public IHttpActionResult DeleteMeeting(int meetingID)
        {
            var response = meetingService.DeleteMeeting(meetingID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }
    }
}