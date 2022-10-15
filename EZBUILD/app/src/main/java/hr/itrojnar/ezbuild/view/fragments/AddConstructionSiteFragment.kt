package hr.itrojnar.ezbuild.view.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.DialogCustomImageSelectionBinding
import hr.itrojnar.ezbuild.databinding.DialogCustomListBinding
import hr.itrojnar.ezbuild.databinding.FragmentAddConstructionSiteBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.constructionSite.CreateConstructionSiteRequest
import hr.itrojnar.ezbuild.model.messaging.employee.EmployeesForFirmResponse
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.CSAssignedEmployeesAdapter
import hr.itrojnar.ezbuild.view.adapters.CustomListEmployeeAdapter
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddConstructionSiteFragment : Fragment() {

    private lateinit var mBinding: FragmentAddConstructionSiteBinding
    private lateinit var mPreferences: SharedPreferences
    private lateinit var mCurrentLocale: String
    private var mImagePath: String = ""
    private lateinit var mBase64Encoded: String

    private lateinit var mCustomListDialog: Dialog

    private var mCSManagerID: Int? = null
    private lateinit var mEngineers: ArrayList<Employee>
    private lateinit var mUnassignedEmployees: ArrayList<Employee>
    private lateinit var mAssignedEmployees: ArrayList<Employee>

    private lateinit var mAssignedEmployeesAdapter: CSAssignedEmployeesAdapter

    private var mLatitude: Double? = null
    private var mLongitude: Double? = null

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentAddConstructionSiteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        (activity as MainActivity).useUpButton()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        if(!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, resources.getString(R.string.google_maps_api_key))
        }
        mCurrentLocale = Locale.getDefault().displayLanguage
        setupListeners()
    }

    private fun setupListeners() {
        mBinding.ivAddCsImage.setOnClickListener {
            customImageSelectionDialog()
        }
        mBinding.etAddCsFullAddress.setOnClickListener {
            try {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireContext())
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e:Exception) {

            }
        }
        mBinding.etAddCsManager.setOnClickListener {
            /*if (mCurrentLocale == "hrvatski") {
                customItemsDialog(getString(R.string.title_select_employee_type), Constants.employeeTypesHR(), Constants.EMPLOYEE_TYPE)
            } else {
                customItemsDialog(getString(R.string.title_select_employee_type), Constants.employeeTypesEN(), Constants.EMPLOYEE_TYPE)
            }*/
            customItemsDialog(getString(R.string.title_select_engineer), mEngineers, Constants.CS_MANAGER)
        }
        mBinding.btnAddCsUnnasignedEmployee.setOnClickListener {
            customItemsDialog(getString(R.string.title_select_unassigned_employee), mUnassignedEmployees, Constants.UNASSIGNED_EMPLOYEE)
        }
        mBinding.btnAddCs.setOnClickListener {
            validateEntries()
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(requireContext())
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvDialogCamera.setOnClickListener {

            Dexter.withContext(requireContext()).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, CAMERA)
                        } else {
                            showRationalDialogForPermissions()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()

            dialog.dismiss()
        }
        binding.tvDialogGallery.setOnClickListener {

            Dexter.withContext(requireContext()).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(requireContext(), "You have denied gallery permission.", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()

            dialog.dismiss()
        }

        dialog.show()
    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            Constants.EMPLOYEE_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etAddCsManager.setText(item)
            }
        }
    }

    fun selectedConstructionSiteManager(item: Employee, selection: String) {
        when (selection) {
            Constants.CS_MANAGER -> {
                mCustomListDialog.dismiss()
                mCSManagerID = item.idEmployee
                mBinding.etAddCsManager.setText(item.fullName)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail : Bitmap = data.extras!!.get("data") as Bitmap
                    //mBinding.ivCsImage.setImageBitmap(thumbnail)

                    Glide.with(requireActivity())
                        .load(thumbnail)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                @Nullable e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // log exception
                                Log.e("TAG", "Error loading image", e)
                                return false // important to return false so the error placeholder can be placed
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {

                                val bitmap: Bitmap = resource.toBitmap()

                                mImagePath = saveImageToInternalStorage(bitmap)
                                val fileImage = File(mImagePath)

                                compressFileImage(fileImage)

                                Log.i("ImagePath", mImagePath)
                                return false
                            }
                        })
                        //.placeholder()
                        .into(mBinding.ivCsImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)

                    mBinding.ivAddCsImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
                }
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data
                    //mBinding.ivCsImage.setImageURI(selectedPhotoUri)

                    Glide.with(requireActivity())
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.placeholder()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                @Nullable e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // log exception
                                Log.e("TAG", "Error loading image", e)
                                return false // important to return false so the error placeholder can be placed
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {

                                val bitmap: Bitmap = resource.toBitmap()
                                mImagePath = saveImageToInternalStorage(bitmap)
                                val fileImage = File(mImagePath)

                                compressFileImage(fileImage)

                                Log.i("ImagePath", mImagePath)
                                return false
                            }
                        })
                        .into(mBinding.ivCsImage)

                    mBinding.ivAddCsImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
                }
            }
        }
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode != Activity.RESULT_CANCELED) {
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)
            mBinding.etAddCsFullAddress.setText(place.address)
            mLatitude = place.latLng!!.latitude
            mLongitude = place.latLng!!.longitude
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("cancelled", "User cancelled image selection")
        }
    }

    private fun compressFileImage(fileImage: File) {
        lifecycleScope.launch {
            val compressedImageFile = Compressor.compress(requireContext(), fileImage, Dispatchers.Main)
            mBase64Encoded = Base64.encodeToString(compressedImageFile.readBytes(), Base64.NO_WRAP)
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.msg_permissions_turned_off))
            .setPositiveButton(getString(R.string.btn_go_to_settings))
            {_,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel)) {dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun customItemsDialog(title: String, itemsList: List<Employee>, selection: String) {
        mCustomListDialog = Dialog(requireContext())
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvDialogCustomListTitle.text = title

        binding.rvDialogCustomList.layoutManager = LinearLayoutManager(requireContext())

        if (mUnassignedEmployees.isEmpty() && selection == Constants.UNASSIGNED_EMPLOYEE) {
            binding.tvCustomListNoMoreUnassignedEmployees.visibility = View.VISIBLE
        }

        val adapter = CustomListEmployeeAdapter(requireActivity(), this@AddConstructionSiteFragment, itemsList, selection)
        binding.rvDialogCustomList.adapter = adapter
        mCustomListDialog.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) : String {
        val wrapper = ContextWrapper(activity?.applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun validateEntries() {

        val fullAddress = mBinding.etAddCsFullAddress.text.toString().trim()
        val csManager = mBinding.etAddCsManager.text.toString().trim()
        val employeeIDs = ArrayList<Int>()
        mAssignedEmployees.forEach {
            employeeIDs.add(it.idEmployee!!)
        }

        when {

            TextUtils.isEmpty(mImagePath) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_image),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(fullAddress) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_cs_address),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(csManager) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_cs_manager),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val constructionSite = CreateConstructionSiteRequest(
                    mBase64Encoded,
                    fullAddress,
                    mLatitude,
                    mLongitude,
                    mBinding.cbAddCsActiveCs.isChecked,
                    mCSManagerID,
                    mPreferences.getInt(Constants.USER_FIRM_ID, 0),
                    employeeIDs
                )

                uploadConstructionSite(constructionSite)
            }
        }
    }

    private fun uploadConstructionSite(constructionSite: CreateConstructionSiteRequest) {

        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.createConstructionSite(constructionSite).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    findNavController().navigate(AddConstructionSiteFragmentDirections.actionAddConstructionSiteFragmentToNavigationConstructionSites())
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                Log.e("UPLOAD ERROR", "Error uploading construction site to API")
            }
        })
    }

    private fun loadData() {
        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.getEngineersForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    response.body()?.employees.let {
                        mEngineers = ArrayList(it)
                        mEngineers.sortBy { e -> e.fullName }
                    }
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting engineers from API.", t)
            }
        })

        ezBuildAPIInterface.getUnassignedEmployeesForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    response.body()?.employees.let {
                        mUnassignedEmployees = ArrayList(it)
                        mUnassignedEmployees.sortBy { e -> e.fullName }
                    }

                    setupRecyclerView()
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting unassigned employees from API.", t)
            }
        })
    }

    private fun setupRecyclerView() {
        mAssignedEmployees = ArrayList<Employee>()
        mBinding.rvAddCsAssignedEmployees.layoutManager = LinearLayoutManager(requireContext())
        mAssignedEmployeesAdapter = CSAssignedEmployeesAdapter(this@AddConstructionSiteFragment, ArrayList<Employee>())
        mBinding.rvAddCsAssignedEmployees.adapter = mAssignedEmployeesAdapter
    }

    fun addUnassignedEmployeeToRecyclerView(employee: Employee) {
        mCustomListDialog.dismiss()

        mAssignedEmployees.add(employee)
        mUnassignedEmployees.remove(employee)
        mAssignedEmployeesAdapter.addUnassignedEmployee(employee)

        mBinding.tvAddCsNoUnassignedEmployeesAddedYet.visibility = View.GONE
        mBinding.rvAddCsAssignedEmployees.visibility = View.VISIBLE
    }

    fun removeAssignedEmployeeFromRecyclerView(employee: Employee) {
        mAssignedEmployees.remove(employee)
        mUnassignedEmployees.add(employee)
        mUnassignedEmployees.sortBy { e -> e.fullName }
        mAssignedEmployeesAdapter.removeAssignedEmployee(employee)

        if (mAssignedEmployeesAdapter.itemCount == 0) {
            mBinding.tvAddCsNoUnassignedEmployeesAddedYet.visibility = View.VISIBLE
            mBinding.rvAddCsAssignedEmployees.visibility = View.GONE
        }
    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "EzBuildImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
}