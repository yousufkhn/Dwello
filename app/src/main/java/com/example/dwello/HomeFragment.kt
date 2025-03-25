package com.example.dwello

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dwello.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.example.dwello.Property

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var databaseRef: DatabaseReference
    private val propertyList = mutableListOf<Property>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.rvProperties.layoutManager = LinearLayoutManager(requireContext())
        propertyAdapter = PropertyAdapter(propertyList)
        binding.rvProperties.adapter = propertyAdapter

        fetchProperties()

        return binding.root
    }

    private fun fetchProperties() {
        databaseRef = FirebaseDatabase.getInstance().getReference("properties")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                propertyList.clear()
                for (propertySnap in snapshot.children) {
                    val property = propertySnap.getValue(Property::class.java)
                    property?.let { propertyList.add(it) }
                }
                propertyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
