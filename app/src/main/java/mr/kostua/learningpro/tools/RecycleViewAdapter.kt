package mr.kostua.learningpro.tools

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
class RecycleViewAdapter<D>(private val data: List<D>, private val layoutRes: Int, private val binder: ViewHolderBinder<D>)
    : RecyclerView.Adapter<RecycleViewAdapter<D>.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewAdapter<D>.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false), binder)
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: RecycleViewAdapter<D>.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val view: View, private val binder: ViewHolderBinder<D>) : RecyclerView.ViewHolder(view) {
        init {
            binder.initializeListViews(this.view)
        }

        fun bind(item: D) {
            binder.bind(item)
        }
    }
}