package br.com.github.sample.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.github.sample.R
import br.com.github.sample.extensions.createParcel
import br.com.github.sample.view.AbstractView
import java.util.*

open class RecyclerViewAdapter<M : Parcelable, V: AbstractView<M>>(context: Context) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder<V>>() {

    internal var inflater: LayoutInflater

    var layoutResId: Int = 0

    var items: ArrayList<M> = ArrayList()
        internal set

    var isLoading = false
        set (loading) {
            val old = this.isLoading

            field = loading

            if (old != loading) {
                var size = items.size

                if (header != null) {
                    size++
                }

                if (loading)
                    notifyItemInserted(size)
                else
                    notifyItemRemoved(size)
            }
        }

    var clickListener: (View.(m: M) -> Unit)? = null
    var header: View? = null
        set(view) {
            val old = field

            field = view

            old?.let {
                notifyItemChanged(0)
            } ?: notifyItemInserted(0)
        }

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<V> {
        when (viewType) {
            LOADING_TYPE -> return RecyclerViewHolder(inflater.inflate(R.layout.list_item_loading, parent, false))
            HEADER_TYPE -> return RecyclerViewHolder(header!!)
            else -> {
                return RecyclerViewHolder<V>(inflater.inflate(getLayoutResForViewType(viewType), parent, false)).apply {
                    clickListener?.apply {
                        itemView.setOnClickListener { view ->
                            val m = (view as V).get()
                            invoke(view, m)
                        }
                    }

                    onPostCreateViewHolder(this, parent)
                }
            }
        }
    }

    internal fun onPostCreateViewHolder(holder: RecyclerViewHolder<V>, parent: ViewGroup) { }

    @LayoutRes
    internal open fun getLayoutResForViewType(viewType: Int): Int = layoutResId

    override fun onBindViewHolder(holder: RecyclerViewHolder<V>, position: Int) {
        when (holder.itemViewType) {
            LOADING_TYPE -> {
                val lp = holder.itemView.layoutParams as RecyclerView.LayoutParams

                with (lp) {
                    if (items.size == 0) {
                        height = RecyclerView.LayoutParams.MATCH_PARENT
                        width = RecyclerView.LayoutParams.MATCH_PARENT
                    } else {
                        height = RecyclerView.LayoutParams.WRAP_CONTENT
                        width = RecyclerView.LayoutParams.MATCH_PARENT
                    }
                }
            }
            HEADER_TYPE -> { }
            else -> {
                var pos = position

                if (header != null) {
                    pos--
                }

                val m = items[pos]

                bind(holder.get(), m, pos)

                onPostBindViewHolder(holder, m, pos)
            }
        }
    }

    val lastItem: M?
        get() = items.lastOrNull()

    val firstItem: M?
        get() = items.firstOrNull()

    internal fun bind(v: V, m: M, position: Int) {
        v.bind(m, position, position == (count - 1))
    }

    internal fun onPostBindViewHolder(holder: RecyclerViewHolder<V>, m: M, position: Int) { }

    override fun getItemViewType(position: Int): Int {
        if (isLoading && position == itemCount - 1) {
            return LOADING_TYPE
        } else if (position == 0 && header != null) {
            return HEADER_TYPE
        }

        return getViewTypeForPosition(position)
    }

    open internal fun getViewTypeForPosition(position: Int) = ITEM_TYPE

    fun update(m: M) {
        val indexOf = items.indexOf(m)

        if (indexOf > -1) {
            notifyItemChanged(if (header != null) indexOf + 1 else indexOf)
        }
    }

    val count: Int
        get() = items.size

    fun clear() {
        val previousSize = items.size
        val start = if (header != null) 1 else 0

        items.clear()
        notifyItemRangeRemoved(start, previousSize)
    }

    fun addAll(list: List<M>) {
        var previous = this.items.size

        this.items.addAll(list)

        if (header != null) {
            previous++
        }

        notifyItemRangeInserted(previous, list.size)
    }

    override fun getItemCount(): Int {
        var count = count

        if (isLoading) {
            count++
        }

        if (header != null) {
            count++
        }

        return count
    }

    @Suppress("UNCHECKED_CAST")
    fun onRestoreState(savedInstanceState: Parcelable) {
        if (savedInstanceState is SavedState<*>) {
            items = savedInstanceState.let { it.list as? ArrayList<M>? } ?: ArrayList()
        }
    }

    fun onSaveInstanceState() = SavedState(items)

    data class SavedState<M: Parcelable> (var list: ArrayList<M>): Parcelable {
        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeTypedList(list)
        }

        override fun describeContents(): Int = 0

        protected constructor(parcelIn: Parcel): this(parcelIn.createTypedArrayList(SavedState.CREATOR) as? ArrayList<M> ?: ArrayList<M>())

        companion object {
            @JvmField @Suppress("unused")
            val CREATOR = createParcel { SavedState<Parcelable>(it) }
        }
    }

    class RecyclerViewHolder<V>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @Suppress("UNCHECKED_CAST")
        fun get(): V = itemView as V
    }

    companion object {
        private val LOADING_TYPE = 0
        private val HEADER_TYPE = 1
        private val ITEM_TYPE = 2
    }
}
