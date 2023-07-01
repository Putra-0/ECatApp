import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.e_catapp.models.Jenis

class JenisAdapter(context: Context, private val jenisList: List<Jenis>) :
    ArrayAdapter<Jenis>(context, 0, jenisList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            android.R.layout.simple_spinner_item, parent, false
        )

        val jenis = jenisList[position]
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = jenis.nama_type

        return view
    }

    fun getPositionById(idJenis: Int): Int {
        for (index in 0 until jenisList.size) {
            if (jenisList[index].id == idJenis) {
                return index
            }
        }
        return 0
    }
}
