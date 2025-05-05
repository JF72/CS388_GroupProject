import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.taro.R
import com.example.taro.components.DateCard

class DateCardAdapter(
    private val items: MutableList<Triple<String, String, String>>
) : RecyclerView.Adapter<DateCardAdapter.DateCardViewHolder>() {

    class DateCardViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateCardViewHolder {
        // Inflate ComposeView and pass it to the ViewHolder
        val composeView = ComposeView(parent.context)
        return DateCardViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: DateCardViewHolder, position: Int) {
        // Get the item
        val (day, month, weekday) = items[position]

        // Set content using Jetpack Compose
        holder.composeView.setContent {
            DateCard(day = day, month = month, weekday = weekday)
        }
    }

    override fun getItemCount() = items.size
}