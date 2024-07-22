package com.lib.admoblib.nativeAds

import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Kashif Ali
 * on 7/11/22.
 */
open class RecyclerViewAdapterWrapper(
    private val wrapped: RecyclerView.Adapter<*>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        wrapped.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
        })
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return wrapped.onCreateViewHolder(parent, viewType)
    }

    @NonNull
    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        wrapped.onBindViewHolder(holder as Nothing, position)
    }

    override fun getItemCount(): Int {
        return wrapped.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return wrapped.getItemViewType(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        wrapped.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return wrapped.getItemId(position)
    }

    override fun onViewRecycled(@NonNull holder: RecyclerView.ViewHolder) {
        wrapped.onViewRecycled(holder as Nothing)
    }

    override fun onFailedToRecycleView(@NonNull holder: RecyclerView.ViewHolder): Boolean {
        return wrapped.onFailedToRecycleView(holder as Nothing)
    }

    override fun onViewAttachedToWindow(@NonNull holder: RecyclerView.ViewHolder) {
        wrapped.onViewAttachedToWindow(holder as Nothing)
    }

    override fun onViewDetachedFromWindow(@NonNull holder: RecyclerView.ViewHolder) {
        wrapped.onViewDetachedFromWindow(holder as Nothing)
    }

    override fun registerAdapterDataObserver(@NonNull observer: RecyclerView.AdapterDataObserver) {
        wrapped.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(@NonNull observer: RecyclerView.AdapterDataObserver) {
        wrapped.unregisterAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(@NonNull recyclerView: RecyclerView) {
        wrapped.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(@NonNull recyclerView: RecyclerView) {
        wrapped.onDetachedFromRecyclerView(recyclerView)
    }

    fun getWrappedAdapter(): RecyclerView.Adapter<*> {
        return wrapped
    }
}
