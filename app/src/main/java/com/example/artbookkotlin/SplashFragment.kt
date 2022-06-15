package com.example.artbookkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artbookkotlin.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var artAdapter: ArtAdapter
    private lateinit var artList: ArrayList<Art>
    private lateinit var binding: FragmentSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        artList = ArrayList()
        artAdapter = ArtAdapter(requireContext(), artList)
        binding.recyclerViewHolder.adapter = artAdapter


        binding.recyclerViewHolder.layoutManager = LinearLayoutManager(context)
        getValuesFromSqlite()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getValuesFromSqlite() {

        try {
            val database =
                requireActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM $DATABASE_TABLE_NAME", null)
            val artNameX = cursor.getColumnIndex("art_name")
            val idX = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val artName = cursor.getString(artNameX)
                val id = cursor.getInt(idX)

                val artModel = Art(artName, id)
                artList.add(artModel)


            }
            cursor.close()
            artAdapter.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_art_book) {
            val action = SplashFragmentDirections.actionSplashFragmentToArtAddFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }

}
