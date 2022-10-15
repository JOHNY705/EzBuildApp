package hr.itrojnar.ezbuild.utils

object Constants {

    /*NETWORK*/
    const val USERS : String = "users"
    const val BASE_URL : String = "http://10.0.2.2:57660/api/"

    const val API_EMPLOYEES : String = "employees"
    const val API_EMPLOYEE : String = "employee"
    const val API_FIRM : String = "firm"
    const val API_CONSTRUCTION_SITES : String = "constructionSites"
    const val API_ENGINEERS : String = "engineers"
    const val API_UNASSIGNED_EMPLOYEES : String = "unassignedEmployees"
    const val API_CONSTRUCTION_SITE : String = "constructionSite"
    const val API_DIARY_ENTRY : String  = "diaryEntry"
    const val API_CONSTRUCTION_SITES_NO_IMAGE : String = "constructionSitesNoImage"
    const val API_EMPLOYEE_HOURS : String = "employeeHours"
    const val API_PHYSICAL_WORKERS : String = "physicalWorkers"
    const val API_WAREHOUSE_MANAGER : String = "warehouseManager"
    const val API_MEETINGS : String = "meetings"
    const val API_WAREHOUSE : String = "warehouse"
    const val API_EQUIPMENT : String = "equipment"
    const val API_EQUIPMENT_HISTORY : String = "equipmentHistory"
    const val API_LEASED_EQUIPMENT : String = "leasedEquipment"
    const val API_DELETE : String = "delete"
    const val API_UPDATE : String = "update"

    /*FRAGMENTS*/
    const val FRAGMENT_CONSTRUCTION_SITES = "ConstructionSitesFragment"
    const val FRAGMENT_CONSTRUCTION_SITE_DETAILS= "ConstructionSiteDetailsFragment"
    const val FRAGMENT_EQUIPMENT = "EquipmentFragment"
    const val FRAGMENT_EQUIPMENT_DETAILS = "EquipmentDetailsFragment"
    const val FRAGMENT_EQUIPMENT_LEASED = "EquipmentLeasedFragment"
    const val FRAGMENT_LEASED_EQUIPMENT_DETAILS = "LeasedEquipmentDetailsFragment"
    const val FRAGMENT_EMPLOYEES = "EmployeesFragment"
    const val FRAGMENT_EMPLOYEE_DETAILS = "EmployeeDetailsFragment"

    /*PREFS*/
    const val EZBUILD_PREFERENCES : String = "EzBuildPrefs"
    const val USER_API_ID : String = "user_api_id"
    const val USER_FIREBASE_ID : String = "user_uid"
    const val USER_FIRM_ID : String = "user_firm_id"
    const val USER_TYPE_ID : String = "user_type_id"
    const val USER_EMAIL : String = "user_email"
    const val USER_FULL_NAME : String = "user_full_name"
    const val USER_PHONE_NUMBER : String = "user_phone_number"

    /*SELECTION*/
    const val CS_MANAGER : String = "ConstructionSiteManager"
    const val UNASSIGNED_EMPLOYEE : String = "UnassignedEmployee"
    const val WAREHOUSE_MANAGER : String = "WarehouseManager"
    const val PHYSICAL_WORKER : String = "PhysicalWorker"

    /*EXTRA*/
    const val EXTRA_CS_DETAILS : String = "ExtraConstructionSiteDetails"
    const val EXTRA_ALL_CS_DETAILS : String = "ExtraAllConstructionSiteDetails"

    /*LOCALE*/
    const val LOCALE_CROATIAN: String = "hrvatski"

    /*TYPE*/
    const val EMPLOYEE_TYPE : String = "EmployeeType"

    const val DIRECTOR_HR : String = "Direktor"
    const val ENGINEER_HR : String = "Inženjer"
    const val WAREHOUSE_MANAGER_HR : String = "Voditelj skladišta"
    const val PHYSICAL_WORKER_HR : String = "Fizički radnik"

    const val DIRECTOR_EN : String = "Director"
    const val ENGINEER_EN : String = "Engineer"
    const val WAREHOUSE_MANAGER_EN : String = "Warehouse manager"
    const val PHYSICAL_WORKER_EN : String = "Physical worker"

    fun employeeTypesHR() : ArrayList<String> {
        val list = ArrayList<String>()
        list.add(DIRECTOR_HR)
        list.add(ENGINEER_HR)
        list.add(WAREHOUSE_MANAGER_HR)
        list.add(PHYSICAL_WORKER_HR)
        return list
    }

    fun employeeTypesEN() : ArrayList<String> {
        val list = ArrayList<String>()
        list.add(DIRECTOR_EN)
        list.add(ENGINEER_EN)
        list.add(WAREHOUSE_MANAGER_EN)
        list.add(PHYSICAL_WORKER_EN)
        return list
    }
}