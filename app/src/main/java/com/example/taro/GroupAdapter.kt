package com.example.taro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(private val groupList: List<UserGroup>) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.groupNameText)
        val groupCode: TextView = itemView.findViewById(R.id.groupCodeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taro_group_item, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]
        holder.groupName.text = group.name
        holder.groupCode.text = "Code: ${group.code}"
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
