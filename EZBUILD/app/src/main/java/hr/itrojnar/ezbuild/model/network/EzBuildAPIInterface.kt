package hr.itrojnar.ezbuild.model.network

import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.constructionSite.*
import hr.itrojnar.ezbuild.model.messaging.diaryEntry.CreateDiaryEntryRequest
import hr.itrojnar.ezbuild.model.messaging.diaryEntry.UpdateDiaryEntryRequest
import hr.itrojnar.ezbuild.model.messaging.employee.*
import hr.itrojnar.ezbuild.model.messaging.employeeHours.CreateEmployeeHours
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursMonthResponse
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursResponse
import hr.itrojnar.ezbuild.model.messaging.meeting.CreateMeetingRequest
import hr.itrojnar.ezbuild.model.messaging.meeting.MeetingsForEmployeeResponse
import hr.itrojnar.ezbuild.model.messaging.meeting.UpdateMeetingRequest
import hr.itrojnar.ezbuild.model.messaging.warehouse.*
import hr.itrojnar.ezbuild.utils.Constants
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface EzBuildAPIInterface {

    companion object {

        fun create() : EzBuildAPIInterface {

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(EzBuildAPIInterface::class.java)
        }
    }

    @GET(Constants.API_EMPLOYEE + "/{firebaseUID}")
    @Headers("Host: localhost")
    fun getRegisteredEmployeeDetails(@Path("firebaseUID") firebaseUID : String) : Call<RegisteredEmployeeDetailsResponse>

    @GET(Constants.API_EMPLOYEES + "/" + Constants.API_FIRM + "/{id}")
    @Headers("Host: localhost")
    fun getEmployeesForFirm(@Path("id") id : Int) : Call<EmployeesForFirmResponse>

    @GET(Constants.API_EMPLOYEES + "/" + Constants.API_ENGINEERS + "/{id}")
    @Headers("Host: localhost")
    fun getEngineersForFirm(@Path("id") id : Int) : Call<EmployeesForFirmResponse>

    @GET(Constants.API_EMPLOYEES + "/" + Constants.API_PHYSICAL_WORKERS + "/{id}")
    @Headers("Host: localhost")
    fun getPhysicalWorkersForFirm(@Path("id") id : Int) : Call<EmployeesForFirmResponse>

    @GET(Constants.API_EMPLOYEES + "/" + Constants.API_WAREHOUSE_MANAGER + "/{id}")
    @Headers("Host: localhost")
    fun getWarehouseManagerForFirm(@Path("id") id : Int) : Call<EmployeesForFirmResponse>

    @GET(Constants.API_EMPLOYEES + "/" + Constants.API_UNASSIGNED_EMPLOYEES + "/{id}")
    @Headers("Host: localhost")
    fun getUnassignedEmployeesForFirm(@Path("id") id : Int) : Call<EmployeesForFirmResponse>

    @POST(Constants.API_FIRM)
    @Headers("Host: localhost")
    fun registerFirmAndUser(@Body firmAndUserRequest: RegisterFirmAndUserRequest) : Call<RegisteredEmployeeDetailsResponse>

    @GET(Constants.API_CONSTRUCTION_SITES + "/" + Constants.API_FIRM + "/{id}")
    @Headers("Host: localhost")
    fun getConstructionSitesForFirm(@Path("id") id : Int) : Call<ConstructionSitesResponse>

    @GET(Constants.API_CONSTRUCTION_SITES_NO_IMAGE + "/" + Constants.API_FIRM + "/{id}")
    @Headers("Host: localhost")
    fun getConstructionSitesForFirmNoImage(@Path("id") id : Int) : Call<ConstructionSitesResponse>

    @GET(Constants.API_CONSTRUCTION_SITES + "/" + Constants.API_EMPLOYEE + "/{id}")
    @Headers("Host: localhost")
    fun getConstructionSitesForEmployee(@Path("id") id : Int) : Call<ConstructionSitesResponse>

    @GET(Constants.API_CONSTRUCTION_SITE + "/{id}")
    @Headers("Host: localhost")
    fun getConstructionSite(@Path("id") id : Int) : Call<GetConstructionSiteResponse>

    @POST(Constants.API_CONSTRUCTION_SITE)
    @Headers("Host: localhost")
    fun createConstructionSite(@Body constructionSiteRequest: CreateConstructionSiteRequest) : Call<BaseResponse>

    @POST(Constants.API_CONSTRUCTION_SITE + "/" + Constants.API_DIARY_ENTRY)
    @Headers("Host: localhost")
    fun createDiaryEntryForConstructionSite(@Body createDiaryEntryRequest: CreateDiaryEntryRequest) : Call<BaseResponse>

    @DELETE(Constants.API_CONSTRUCTION_SITE + "/" + Constants.API_DIARY_ENTRY + "/{id}")
    @Headers("Host: localhost")
    fun deleteConstructionSiteDiaryEntry(@Path("id") id : Int) : Call<BaseResponse>

    @PUT(Constants.API_CONSTRUCTION_SITE + "/" + Constants.API_DIARY_ENTRY)
    @Headers("Host: localhost")
    fun updateDiaryEntryForConstructionSite(@Body updateDiaryEntryRequest: UpdateDiaryEntryRequest) : Call<BaseResponse>

    @DELETE(Constants.API_CONSTRUCTION_SITE + "/{id}")
    @Headers("Host: localhost")
    fun deleteConstructionSite(@Path("id") id : Int) : Call<BaseResponse>

    @PUT(Constants.API_CONSTRUCTION_SITE)
    @Headers("Host: localhost")
    fun updateConstructionSite(@Body updateConstructionSiteRequest: UpdateConstructionSiteRequest) : Call<UpdateConstructionSiteResponse>

    @GET(Constants.API_EMPLOYEE_HOURS + "/" + Constants.API_EMPLOYEE + "/{id}")
    @Headers("Host: localhost")
    fun getEmployeeHoursMonthForEmployee(@Path("id") id : Int) : Call<EmployeeHoursMonthResponse>

    @POST(Constants.API_EMPLOYEE)
    @Headers("Host: localhost")
    fun createEmployee(@Body createEmployeeRequest: CreateEmployeeRequest) : Call<BaseResponse>

    @GET(Constants.API_EMPLOYEE_HOURS + "/" + Constants.API_CONSTRUCTION_SITE + "/{id}")
    @Headers("Host: localhost")
    fun getEmployeeHoursForConstructionSite(@Path("id") id : Int) : Call<EmployeeHoursResponse>

    @POST(Constants.API_EMPLOYEE_HOURS)
    @Headers("Host: localhost")
    fun createEmployeeHours(@Body createEmployeeHours: CreateEmployeeHours) : Call<BaseResponse>

    @GET(Constants.API_EMPLOYEE_HOURS + "/" + Constants.API_ENGINEERS + "/{id}")
    @Headers("Host: localhost")
    fun getEmployeeHoursForEngineers(@Path("id") id : Int) : Call<EmployeeHoursResponse>

    @GET(Constants.API_EMPLOYEE_HOURS + "/" + Constants.API_PHYSICAL_WORKERS + "/{id}")
    @Headers("Host: localhost")
    fun getEmployeeHoursForPhysicalWorkers(@Path("id") id : Int) : Call<EmployeeHoursResponse>

    @GET(Constants.API_EMPLOYEE_HOURS + "/" + Constants.API_WAREHOUSE_MANAGER + "/{id}")
    @Headers("Host: localhost")
    fun getEmployeeHoursForWarehouseManager(@Path("id") id : Int) : Call<EmployeeHoursResponse>

    @GET(Constants.API_MEETINGS + "/{id}")
    @Headers("Host: localhost")
    fun getMeetingsForEmployee(@Path("id") id : Int) : Call<MeetingsForEmployeeResponse>

    @DELETE(Constants.API_MEETINGS + "/{id}")
    @Headers("Host: localhost")
    fun deleteMeeting(@Path("id") id : Int) : Call<BaseResponse>

    @POST(Constants.API_MEETINGS)
    @Headers("Host: localhost")
    fun createMeeting(@Body createMeetingRequest: CreateMeetingRequest) : Call<BaseResponse>

    @PUT(Constants.API_MEETINGS)
    @Headers("Host: localhost")
    fun updateMeeting(@Body updateMeetingRequest: UpdateMeetingRequest) : Call<BaseResponse>

    @GET(Constants.API_WAREHOUSE + "/{id}")
    @Headers("Host: localhost")
    fun getWarehouseForFirm(@Path("id") id : Int ) : Call<WarehouseForFirmResponse>

    @POST(Constants.API_WAREHOUSE)
    @Headers("Host: localhost")
    fun createUpdateWarehouse(@Body createUpdateWarehouseRequest: CreateUpdateWarehouseRequest) : Call<BaseResponse>

    @DELETE(Constants.API_WAREHOUSE + "/{id}")
    @Headers("Host: localhost")
    fun deleteWarehouse(@Path("id") id : Int) : Call<BaseResponse>

    @POST(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT)
    @Headers("Host: localhost")
    fun createEquipment(@Body createEquipmentRequest: CreateEquipmentRequest) : Call<BaseResponse>

    @GET(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT + "/{id}")
    @Headers("Host: localhost")
    fun getEquipmentForWarehouse(@Path("id") id: Int) : Call<EquipmentForWarehouseResponse>

    @PUT(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT)
    @Headers("Host: localhost")
    fun updateEquipment(@Body updateEquipmentRequest: UpdateEquipmentRequest) : Call<BaseResponse>

    @DELETE(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT + "/{id}")
    @Headers("Host: localhost")
    fun deleteEquipment(@Path("id") id : Int) : Call<BaseResponse>

    @GET(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT_HISTORY + "/{id}")
    @Headers("Host: localhost")
    fun getEquipmentHistoryForWarehouse(@Path("id") id: Int) : Call<EquipmentHistoryForWarehouseResponse>

    @POST(Constants.API_WAREHOUSE + "/" + Constants.API_LEASED_EQUIPMENT)
    @Headers("Host: localhost")
    fun getLeasedEquipmentDetails(@Body leasedEquipmentDetailsRequest: LeasedEquipmentDetailsRequest) : Call<EquipmentHistoryForWarehouseResponse>

    @POST(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT_HISTORY)
    @Headers("Host: localhost")
    fun createEquipmentHistoryForWarehouse(@Body createEquipmentHistoryRequest: CreateEquipmentHistoryRequest) : Call<BaseResponse>

    @POST(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT_HISTORY + "/" + Constants.API_DELETE)
    @Headers("Host: localhost")
    fun deleteEquipmentHistory(@Body deleteEquipmentHistoryRequest: DeleteEquipmentHistoryRequest) : Call<BaseResponse>

    @PUT(Constants.API_WAREHOUSE + "/" + Constants.API_EQUIPMENT_HISTORY + "/" + Constants.API_UPDATE)
    @Headers("Host: localhost")
    fun updateEquipmentHistory(@Body updateEquipmentHistoryRequest: UpdateEquipmentHistoryRequest) : Call<BaseResponse>

    @DELETE(Constants.API_EMPLOYEE + "/{id}")
    @Headers("Host: localhost")
    fun deleteEmployee(@Path("id") id : Int) : Call<BaseResponse>

    @PUT(Constants.API_EMPLOYEE)
    @Headers("Host: localhost")
    fun updateEmployee(@Body updateEmployeeRequest: UpdateEmployeeRequest) : Call<BaseResponse>
}