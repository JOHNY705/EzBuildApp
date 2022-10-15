package hr.itrojnar.ezbuild.view.fragments

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEquipmentDetailsBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentEquipmentDetailsBinding
    private lateinit var mEquipment: Equipment

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEquipmentDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        val args: EquipmentDetailsFragmentArgs by navArgs()
        mEquipment = args.equipment
        setupView()
    }

    private fun setupView() {
        mEquipment.base64Image.let {
            Glide.with(requireActivity())
                .asBitmap()
                .load(Base64.decode(mEquipment.base64Image, Base64.DEFAULT))
                .centerCrop()
                .into(mBinding.ivEquipmentDetailsImage)
        }

        mBinding.tvEquipmentDetailsName.text = mEquipment.equipmentName
        mEquipment.quantity?.let {
            if (mEquipment.quantity == 0) {
                mBinding.tvEquipmentDetailsQuantity.text = getString(R.string.lbl_equipment_currently_unavailable)
                mBinding.tvEquipmentDetailsQuantity.setTextColor(resources.getColor(R.color.colorSnackBarError))
            } else {
                mBinding.tvEquipmentDetailsQuantity.text = mEquipment.quantity.toString()
            }
        }
        mBinding.tvEquipmentDetailsDescription.text = mEquipment.equipmentDescription
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_equipment_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_equipment_details_edit -> {
                val args: EquipmentDetailsFragmentArgs by navArgs()
                findNavController().navigate(EquipmentDetailsFragmentDirections.actionEquipmentDetailsFragmentToEditEquipmentFragment(mEquipment, Constants.FRAGMENT_EQUIPMENT_DETAILS, args.warehouseID))
            }
            R.id.action_equipment_details_delete -> {
                deleteEquipment(mEquipment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteEquipment(equipmentItem: Equipment) {

        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_equipment))
        builder.setMessage(resources.getString(R.string.msg_delete_equipment_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteEquipment(equipmentItem.idEquipment!!).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {
                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.success_delete_equipment), false)
                        val args: EquipmentDetailsFragmentArgs by navArgs()
                        findNavController().navigate(EquipmentDetailsFragmentDirections.actionEquipmentDetailsFragmentToEquipmentFragment(args.warehouseID))
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_equipment_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while deleting equipment on API.", t)
                }
            })
        }

        builder.setNegativeButton(resources.getString(R.string.btn_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}