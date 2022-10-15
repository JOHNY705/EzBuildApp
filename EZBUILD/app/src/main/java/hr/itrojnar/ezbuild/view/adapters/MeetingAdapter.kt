package hr.itrojnar.ezbuild.view.adapters

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ItemMeetingLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.Meeting
import hr.itrojnar.ezbuild.view.fragments.MeetingsFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MeetingAdapter(
    private val fragment: Fragment,
    private val meetings: List<Meeting>
) : RecyclerView.Adapter<MeetingAdapter.ViewHolder>() {

    class ViewHolder(view: ItemMeetingLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val tvMeetingTitle = view.tvMeetingTitle
        val tvMeetingDate = view.tvMeetingDate
        val tvMeetingStartTime = view.tvMeetingStartTime
        val tvMeetingDuration = view.tvMeetingDuration
        val tvMeetingDescription = view.tvMeetingDescription
        val ivMeetingMenu = view.ivMeetingMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMeetingLayoutBinding = ItemMeetingLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meeting = meetings[position]

        val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

        val formattedDate = LocalDate.parse(meeting.meetingDate, apiFormatter).format(formatter)
        val meetingStartTime = meeting.meetingStartTime?.take(5)
        val meetingDuration = meeting.meetingDuration + " min"

        holder.tvMeetingTitle.text = meeting.title
        holder.tvMeetingDate.text = formattedDate
        holder.tvMeetingStartTime.text = meetingStartTime
        holder.tvMeetingDuration.text = meetingDuration
        holder.tvMeetingDescription.text = meeting.meetingDescription


        holder.ivMeetingMenu.setOnClickListener {

            val wrapper = ContextThemeWrapper(fragment.context, R.style.PopupMenu)
            val popupMenu = PopupMenu(wrapper, holder.ivMeetingMenu, Gravity.RIGHT)
            popupMenu.menuInflater.inflate(R.menu.menu_meeting_adapter, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_meeting_edit) {
                    if (fragment is MeetingsFragment) {
                        fragment.editMeeting(meeting)
                    }
                }
                else if (it.itemId == R.id.action_meeting_site_delete) {
                    if (fragment is MeetingsFragment) {
                        fragment.deleteMeeting(meeting)
                    }
                }
                true
            }

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val popup = fieldMPopup.get(popupMenu)
                popup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(popup, true)
            } catch (e: Exception) {
                Log.e("POPUP MENU", "Error showing menu icons", e)
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return meetings.size
    }
}