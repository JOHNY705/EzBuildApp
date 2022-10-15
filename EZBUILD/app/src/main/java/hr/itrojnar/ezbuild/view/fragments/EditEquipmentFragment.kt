package hr.itrojnar.ezbuild.view.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
import hr.itrojnar.ezbuild.databinding.FragmentEditEquipmentBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.UpdateEquipmentRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
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

class EditEquipmentFragment : Fragment() {

    private lateinit var mBinding: FragmentEditEquipmentBinding

    private var mImagePath: String = ""
    private var mBase64Encoded: String = ""
    private lateinit var mEquipment: Equipment
    private lateinit var mFragment: String

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "EzBuildImages"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditEquipmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()

        val args: EditEquipmentFragmentArgs by navArgs()
        mEquipment = args.equipment
        mFragment = args.fragment

        mBase64Encoded = mEquipment.base64Image!!

        setupView()
    }

    private fun setupView() {

        Glide.with(requireActivity())
            .asBitmap()
            .load(Base64.decode(mEquipment.base64Image, Base64.DEFAULT))
            .centerCrop()
            .into(mBinding.ivEquipmentImage)

        mBinding.etEditEquipmentName.setText(mEquipment.equipmentName)
        mBinding.etEditEquipmentQuantity.setText(mEquipment.quantity.toString())
        mBinding.etEditEquipmentDescription.setText(mEquipment.equipmentDescription)

        mBinding.ivEditEquipmentImage.setOnClickListener {
            customImageSelectionDialog()
        }

        mBinding.btnEditEquipment.setOnClickListener {
            validateEntries()
        }
    }

    private fun validateEntries() {
        val equipmentName = mBinding.etEditEquipmentName.text.toString().trim()
        val equipmentQuantity = mBinding.etEditEquipmentQuantity.text.toString().trim()
        val equipmentDescription = mBinding.etEditEquipmentDescription.text.toString().trim()

        when {

            TextUtils.isEmpty(equipmentName) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_enter_equipment_name),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(equipmentQuantity) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_enter_equipment_quantity),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(equipmentDescription) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_enter_equipment_descripion),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val updateEquipmentRequest = UpdateEquipmentRequest(
                    mEquipment.idEquipment,
                    mBase64Encoded,
                    equipmentName,
                    equipmentQuantity.toInt(),
                    equipmentDescription
                )

                updateEquipment(updateEquipmentRequest)
            }
        }
    }

    private fun updateEquipment(updateEquipmentRequest: UpdateEquipmentRequest) {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.updateEquipment(updateEquipmentRequest).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.update_equipment_success), false)
                    val args: EditEquipmentFragmentArgs by navArgs()
                    if (mFragment == Constants.FRAGMENT_EQUIPMENT) {
                        findNavController().navigate(EditEquipmentFragmentDirections.actionEditEquipmentFragmentToEquipmentFragment(args.warehouseID))
                    }
                    else if (mFragment == Constants.FRAGMENT_EQUIPMENT_DETAILS) {
                        mEquipment.base64Image = mBase64Encoded
                        mEquipment.equipmentName = updateEquipmentRequest.equipmentName!!
                        mEquipment.quantity = updateEquipmentRequest.quantity!!
                        mEquipment.equipmentDescription = updateEquipmentRequest.equipmentDescription!!
                        findNavController().navigate(EditEquipmentFragmentDirections.actionEditEquipmentFragmentToEquipmentDetailsFragment(mEquipment, args.warehouseID))
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.update_equipment_error), true)
                Log.e("EQUIPMENT ERROR", "Error updating equipment.")
            }
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail : Bitmap = data.extras!!.get("data") as Bitmap

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
                        .into(mBinding.ivEquipmentImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)

                    mBinding.ivEditEquipmentImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
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
                        .into(mBinding.ivEquipmentImage)

                    mBinding.ivEditEquipmentImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
                }
            }
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
}