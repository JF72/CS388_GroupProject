import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.taro.R
import com.example.taro.components.DateCard

class DateCardAdapter(
    val items: MutableList<Triple<String, String, String>>,
    private var selectedDate: Int = 5,
    private val onDateSelected: (Int, Triple<String,String,String>) -> Unit
) : RecyclerView.Adapter<DateCardAdapter.DateCardViewHolder>() {

    inner class DateCardViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateCardViewHolder {
        return DateCardViewHolder(ComposeView(parent.context))
    }

    override fun onBindViewHolder(holder: DateCardViewHolder, position: Int) {
        val (day, month, weekday) = items[position]
        val isSelected = position == selectedDate

        holder.composeView.setContent{
            DateCard( day = day, month = month, weekday = weekday, isSelected = isSelected){
                val previousDate =  selectedDate
                selectedDate = position
                notifyItemChanged(previousDate)
                notifyItemChanged(selectedDate)
                onDateSelected(position, items[position])
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Triple<String, String, String>>, newSelected: Int) {
        items.clear()
        items.addAll(newItems)
        selectedDate = newSelected
        notifyDataSetChanged()
    }
}
