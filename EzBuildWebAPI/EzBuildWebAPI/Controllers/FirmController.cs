using EzBuildServices.Factory;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Firm;
using System.Web.Http;

namespace EzBuildWebAPI.Controllers
{
    [RoutePrefix("api")]
    public class FirmController : ApiController
    {
        private readonly IFirmService firmService = ObjectFactory.GetFirmService();

        [HttpGet]
        [Route("firm/{firmID}")]
        public IHttpActionResult GetFirmName(int firmID)
        {
            var response = firmService.GetFirmName(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("firm")]
        public IHttpActionResult RegisterFirmAndUser([FromBody] RegisterFirmAndUserRequest request)
        {
            var response = firmService.RegisterFirmAndUser(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("firm")]
        public IHttpActionResult UpdateFirmName([FromBody] UpdateFirmNameRequest request)
        {
            var response = firmService.UpdateFirmName(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }
    }
}
