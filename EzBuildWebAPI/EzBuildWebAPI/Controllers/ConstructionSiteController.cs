using EzBuildServices.Factory;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.ConstructionSites;
using System.Web.Http;

namespace EzBuildWebAPI.Controllers
{
    [RoutePrefix("api")]
    public class ConstructionSiteController : ApiController
    {
        private readonly IConstructionSiteService constructionSiteService = ObjectFactory.GetConstructionSiteService();

        [HttpGet]
        [Route("constructionSites/firm/{firmID}")]
        public IHttpActionResult GetConstructionSitesForFirm(int firmID)
        {
            var response = constructionSiteService.GetConstructionSitesForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("constructionSitesNoImage/firm/{firmID}")]
        public IHttpActionResult GetConstructionSitesForFirmNoImage(int firmID)
        {
            var response = constructionSiteService.GetConstructionSitesForFirmNoImage(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("constructionSites/employee/{employeeID}")]
        public IHttpActionResult GetConstructionSitesForEmployee(int employeeID)
        {
            var response = constructionSiteService.GetConstructionSitesForEmployee(employeeID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("constructionSite/{constructionSiteID}")]
        public IHttpActionResult GetConstructionSite(int constructionSiteID)
        {
            var response = constructionSiteService.GetConstructionSite(constructionSiteID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("constructionSite")]
        public IHttpActionResult UpdateConstructionSite([FromBody] UpdateConstructionSiteRequest request)
        {
            var response = constructionSiteService.UpdateConstructionSite(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("constructionSite/image")]
        public IHttpActionResult UpdateConstructionSiteImage([FromBody] UpdateConstructionSiteImageRequest request)
        {
            var response = constructionSiteService.UpdateConstructionSiteImageRequest(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("constructionSite")]
        public IHttpActionResult CreateConstructionSite([FromBody] CreateConstructionSiteRequest request)
        {
            var response = constructionSiteService.CreateConstructionSite(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("constructionSite/removeEmployee/")]
        public IHttpActionResult RemoveEmployeeFromConstructionSite([FromBody] RemoveEmployeeFromConstructionSiteRequest request)
        {
            var response = constructionSiteService.RemoveEmployeeFromConstructionSite(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("constructionSite/updateCSManager")]
        public IHttpActionResult UpdateCSManagerForConstructionSite([FromBody] UpdateCSManagerRequest request)
        {
            var response = constructionSiteService.UpdateCSManagerForConstructionSite(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpDelete]
        [Route("constructionSite/{constructionSiteID}")]
        public IHttpActionResult DeleteConstructionSite(int constructionSiteID)
        {
            var response = constructionSiteService.DeleteConstructionSite(constructionSiteID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("constructionSite/diaryEntry")]
        public IHttpActionResult CreateCSDiaryEntry([FromBody] CreateCSDiaryEntryRequest request)
        {
            var response = constructionSiteService.CreateCSDiaryEntry(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpDelete]
        [Route("constructionSite/diaryEntry/{diaryEntryID}")]
        public IHttpActionResult DeleteCSDiaryEntry(int diaryEntryID)
        {
            var response = constructionSiteService.DeleteCSDiaryEntry(diaryEntryID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("constructionSite/diaryEntry")]
        public IHttpActionResult UpdateCSDiaryEntry([FromBody] UpdateCSDiaryEntryRequest request)
        {
            var response = constructionSiteService.UpdateCSDiaryEntry(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }
    }
}
