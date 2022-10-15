using EzBuildServices.Factory;
using EzBuildServices.Interfaces;
using EzBuildServicesMessaging.Warehouse;
using System.Web.Http;

namespace EzBuildWebAPI.Controllers
{
    [RoutePrefix("api")]
    public class WarehouseController : ApiController
    {
        private readonly IWarehouseService warehouseService = ObjectFactory.GetWarehouseService();

        [HttpGet]
        [Route("warehouse/{firmID}")]
        public IHttpActionResult GetWarehouse(int firmID)
        {
            var response = warehouseService.GetWarehouse(firmID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("warehouse")]
        public IHttpActionResult CreateUpdateWarehouse([FromBody] CreateUpdateWarehouseRequest request)
        {
            var response = warehouseService.CreateUpdateWarehouse(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("warehouse/equipment/{warehouseID}")]
        public IHttpActionResult CreateUpdateWarehouse(int warehouseID)
        {
            var response = warehouseService.GetEquipmentForWarehouse(warehouseID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("warehouse/equipmentHistory/{warehouseID}")]
        public IHttpActionResult GetEquipmentHistoryForWarehouse(int warehouseID)
        {
            var response = warehouseService.GetEquipmentHistoryForWarehouse(warehouseID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("warehouse/equipment")]
        public IHttpActionResult CreateEquipmentForWarehouse([FromBody] CreateEquipmentRequest request)
        {
            var response = warehouseService.CreateEquipmentForWarehouse(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("warehouse/equipmentHistory")]
        public IHttpActionResult CreateEquipmentHistoryForWarehouse([FromBody] CreateEquipmentHistoryRequest request)
        {
            var response = warehouseService.CreateEquipmentHistoryForWarehouse(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpDelete]
        [Route("warehouse/{warehouseID}")]
        public IHttpActionResult DeleteWarehouse(int warehouseID)
        {
            var response = warehouseService.DeleteWarehouse(warehouseID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("warehouse/equipment")]
        public IHttpActionResult UpdateEquipment([FromBody] UpdateEquipmentRequest request)
        {
            var response = warehouseService.UpdateEquipment(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpDelete]
        [Route("warehouse/equipment/{equipmentID}")]
        public IHttpActionResult DeleteEquipment(int equipmentID)
        {
            var response = warehouseService.DeleteEquipment(equipmentID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("warehouse/leasedEquipment")]
        public IHttpActionResult GetEquipmentHistoryForEmployeeAndDate([FromBody] GetEquipmentHistoryForEmployeeAndDateRequest request)
        {
            var response = warehouseService.GetEquipmentHistoryForEmployeeAndDate(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpGet]
        [Route("warehouse/equipmentHistoryForWarehouse/{warehouseID}")]
        public IHttpActionResult Get(int warehouseID)
        {
            var response = warehouseService.GetEquipmentHistoryForWarehouseResponse(warehouseID);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPost]
        [Route("warehouse/equipmentHistory/delete")]
        public IHttpActionResult DeleteEquipmentHistory([FromBody] DeleteEquipmentHistoryRequest request)
        {
            var response = warehouseService.DeleteEquipmentHistory(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }

        [HttpPut]
        [Route("warehouse/equipmentHistory/update")]
        public IHttpActionResult UpdateEquipmentHistory([FromBody] UpdateEquipmentHistoryRequest request)
        {
            var response = warehouseService.UpdateEquipmentHistory(request);

            if (!response.IsSuccessful)
            {
                return BadRequest(response.Message);
            }

            return Ok(response);
        }
    }
}
