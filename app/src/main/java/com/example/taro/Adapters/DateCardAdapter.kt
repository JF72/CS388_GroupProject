import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.taro.R
import com.example.taro.components.DateCard

class DateCardAdapter(
    private val items: List<Triple<String, String, String>>
) : RecyclerView.Adapter<DateCardAdapter.DateCardViewHolder>() {

    class DateCardViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_compose_item, parent, false) as ComposeView
        return DateCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateCardViewHolder, position: Int) {
        val (day, month, weekday) = items[position]
        holder.composeView.setContent {
            DateCard(day = day, month = month, weekday = weekday)
        }
    }

    override fun getItemCount() = items.size
}
