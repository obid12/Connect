package com.example.connect.main.ui.home.tablayout.news.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.connect.R
import com.example.connect.databinding.AddNewsFragmentBinding
import com.example.connect.login.data.model.DataUser
import com.example.connect.login.data.model.UserResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File


class AddNewsFragment : Fragment() {

    lateinit var binding: AddNewsFragmentBinding
    private val REQUEST_CODE = 100

    private val viewModel: AddNewsViewModel by lazy {
        ViewModelProvider(this).get(AddNewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_news_fragment, container, false)

        viewModel.image.observe(viewLifecycleOwner, {
            Log.v("GMANA", it.toString())
        })
        binding.fabAddImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_DENIED
            ) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_allow_permission),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                selectImageFromGallery()
            }
        }

        binding.include3.backImage.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }

    private fun selectImageFromGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_CODE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonUpload = binding.fabNews

        binding.cancelImagePost.setOnClickListener {
            binding.apply {
                cardAddPost.visibility = View.GONE
                imgAddPost.setImageURI(null)
                viewModel.imageNull()
                fabAddImage.text = "Tambahkan Gambar"
                fabAddImage.setIconResource(R.drawable.ic_baseline_swap_vert_24)
            }
        }

        viewModel.image.observe(viewLifecycleOwner, {
            Log.v("IMAGE", it.toString())
            viewModel.postKirimanDataChanged()
        })

        viewModel.content.observe(viewLifecycleOwner, {
            Log.v("CONTENT", it.toString())
            viewModel.postKirimanDataChanged()
        })

        viewModel.value.observe(viewLifecycleOwner, {
//            if(it.isDataValid){
            if(it!!.isDataValid){
                buttonUpload.isEnabled = true

//                buttonUpload.setOnClickListener {
//                    viewModel.posting(
//                        requireActivity()
//                            .getSharedPreferences("my_data_pref", Context.MODE_PRIVATE)
//                            .getString("token", "").toString(),
//                        viewModel.,
//                        viewModel.content
//
//                    )
//                }
            } else {
                buttonUpload.isEnabled = false
            }
        })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!!.equals(0)){
                    viewModel.contentNull()
                }  else {
                    viewModel.content(p0.toString())
                }

            }
        }

        binding.editText.addTextChangedListener(afterTextChangedListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            var file = data?.data
            var requestBodyFile = RequestBody.create(MediaType.parse("image/*"), File(file!!.path))

            binding.apply {
                viewModel.image(requestBodyFile)
                imgAddPost.setImageURI(file)
                cardAddPost.visibility = View.VISIBLE
                fabAddImage.text = "Ganti Gambar"
                fabAddImage.setIconResource(R.drawable.ic_baseline_swap_vert_24)
            }
        } else {
            binding.apply {
                cardAddPost.visibility = View.GONE
                imgAddPost.setImageURI(data?.data)

                cardAddPost.visibility = View.GONE
                imgAddPost.setImageURI(null)
                viewModel.imageNull()
                fabAddImage.text = "Tambahkan Gambar"
                fabAddImage.setIconResource(R.drawable.ic_baseline_swap_vert_24)
            }

        }
    }

    fun loggedIn(): UserResponse {
        val sharedPreferences = requireActivity()
            .getSharedPreferences("my_data_pref", Context.MODE_PRIVATE)

        val sharedPreferencesDataUser = DataUser(
            sharedPreferences.getInt("id", -1),
            sharedPreferences.getString("name", "").toString(),
            sharedPreferences.getString("email", "").toString(),
            sharedPreferences.getString("email_verified_at", "").toString(),
            sharedPreferences.getString("status", ""),
            sharedPreferences.getString("level", "").toString(),
            sharedPreferences.getString("created_at", "").toString(),
            sharedPreferences.getString("updated_at", "").toString()
        )

        val sharedPreferencesResponse = com.example.connect.login.data.model.response(
            sharedPreferences.getString("token", "").toString(),
            sharedPreferences.getString("token_type", "").toString(),
            sharedPreferencesDataUser
        )

        return UserResponse(
            sharedPreferencesResponse,
            "null"
        )
    }
}
