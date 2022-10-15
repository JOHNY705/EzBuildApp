using EzBuildServices.Factory;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.EmployeeHours;
using System.Web.Http;

namespace EzBuildWebAPI.Controllers
{
    [RoutePrefix("api")]
    public class EmployeeHoursController : ApiController
    {
        private readonly IEmployeeHoursService employeeHoursService = ObjectFactory.GetEmployeeHoursService();

        [HttpGet]
        [Route("employeeHours/{firmID}")]
        public IHttpActionResult GetEmployeeHoursForFirm(int firmID)
        {
            var response = employeeHoursService.GetEmployeeHoursForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employeeHours/constructionSite/{constructionSiteID}")]
        public IHttpActionResult GetEmployeeHoursForCS(int constructionSiteID)
        {
            var response = employeeHoursService.GetEmployeeHoursForCS(constructionSiteID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employeeHours/employee/{employeeID}")]
        public IHttpActionResult GetEmployeeHoursMonthForEmployee(int employeeID)
        {
            var response = employeeHoursService.GetEmployeeHoursMonthForEmployee(employeeID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("employeeHours")]
        public IHttpActionResult CreateEmployeeHoursForDay([FromBody] CreateEmployeeHoursForDayRequest request)
        {
            var response = employeeHoursService.CreateEmployeeHoursForDay(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employeeHours/engineers/{firmID}")]
        public IHttpActionResult GetEmployeeHoursForEngineers(int firmID)
        {
            var response = employeeHoursService.GetEmployeeHoursForEngineers(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employeeHours/physicalWorkers/{firmID}")]
        public IHttpActionResult GetEmployeeHoursForPhysicalWorkers(int firmID)
        {
            var response = employeeHoursService.GetEmployeeHoursForPhysicalWorkers(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employeeHours/warehouseManager/{firmID}")]
        public IHttpActionResult GetEmployeeHoursForWarehouseManager(int firmID)
        {
            var response = employeeHoursService.GetEmployeeHoursForWarehouseManager(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

    }
}
