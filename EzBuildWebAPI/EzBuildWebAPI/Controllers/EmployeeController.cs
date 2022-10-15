using EzBuildServices.Factory;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Employees;
using System.Web.Http;

namespace EzBuildWebAPI.Controllers
{
    [RoutePrefix("api")]
    public class EmployeeController : ApiController
    {
        private readonly IEmployeeService employeeService = ObjectFactory.GetEmployeeService();

        [HttpGet]
        [Route("employees/firm/{firmID}")]
        public IHttpActionResult GetEmployeesForFirm(int firmID)
        {
            var response = employeeService.GetEmployeesForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employees/constructionSite/{constructionSiteID}")]
        public IHttpActionResult GetEmployeesForConstructionSite(int constructionSiteID)
        {
            var response = employeeService.GetEmployeesForConstructionSite(constructionSiteID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employees/engineers/{firmID}")]
        public IHttpActionResult GetEngineersForFirm(int firmID)
        {
            var response = employeeService.GetEngineersForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employees/physicalWorkers/{firmID}")]
        public IHttpActionResult GetPhysicalWorkersForFirm(int firmID)
        {
            var response = employeeService.GetPhysicalWorkersForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employees/warehouseManager/{firmID}")]
        public IHttpActionResult GetWarehouseManagerForFirm(int firmID)
        {
            var response = employeeService.GetWarehouseManagerForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employees/unassignedEmployees/{firmID}")]
        public IHttpActionResult GetUnassignedEmployeesForFirm(int firmID)
        {
            var response = employeeService.GetUnassignedEmployeesForFirm(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("employee/{firebaseUID}")]
        public IHttpActionResult GetRegisteredEmployeeDetails(string firebaseUID)
        {
            var response = employeeService.GetRegisteredEmployeeDetails(firebaseUID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("employee")]
        public IHttpActionResult CreateEmployee([FromBody] CreateEmployeeRequest request)
        {
            var response = employeeService.CreateEmployee(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("employee")]
        public IHttpActionResult UpdateEmployee([FromBody] UpdateEmployeeRequest request)
        {
            var response = employeeService.UpdateEmployee(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpDelete]
        [Route("employee/{employeeID}")]
        public IHttpActionResult DeleteEmployee(int employeeID)
        {
            var response = employeeService.DeleteEmployee(employeeID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }
    }
}