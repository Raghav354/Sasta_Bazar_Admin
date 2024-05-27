package com.example.shoppingappadmin

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingappadmin.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val productModel = ProductModel()
    var categoryList = arrayOf(
        "Dresses", "Tops", "JumpSuits", "Bottoms", "Saree",
        "Kurti","Trouser","Shirt" , "Accessories"
    )
    private var discountedPercent: Double = 0.0

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                binding.rootLayout.setBackgroundColor(Color.LTGRAY)
                binding.mainLayout.visibility = View.GONE
                binding.spinKit.visibility = View.VISIBLE    //Loader appers on the UI.
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                //When data upload on firebase

                Firebase.storage.reference.child("ProductImage/${UUID.randomUUID()}")
                    .putFile(fileUri).addOnCompleteListener {

                        if (it.isSuccessful) {

                            it.result.storage.downloadUrl.addOnSuccessListener {
                                productModel.imageUrl = it.toString()
                                binding.productImage.setImageURI(fileUri)
                                binding.mainLayout.visibility = View.VISIBLE
                                binding.spinKit.visibility = View.GONE
                                binding.rootLayout.setBackgroundColor(Color.WHITE)
                            }

                        }
                    }


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.productImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        var arrayAdapter =
            ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, categoryList)
        binding.category.adapter = arrayAdapter

        binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                productModel.category = categoryList.get(p2)

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


        binding.addProduct.setOnClickListener {
            if (binding.productName.text.toString().isEmpty()) {
                binding.productName.error = "Error:- Add product name"
            } else if (binding.discountprice.text.toString().isEmpty()) {
                binding.discountprice.error = "Error:- Add discount price."
            } else if (binding.disp.text.toString().isEmpty()) {
                binding.disp.error = "Error:- Add product description"
            } else if (binding.disp.text.toString().trim().length < 80) {
                binding.disp.error = "Error:- Need more description."
            } else if (productModel.imageUrl.isEmpty()) {
                Toast.makeText(this@MainActivity, "Add image...!!", Toast.LENGTH_SHORT).show()
            }else if(binding.originalprice.text.toString().isEmpty()){
                binding.originalprice.error = "Error:- Add original price."
            }else if(binding.size.text.toString().isEmpty()) {
                binding.discountprice.error = "Error:- Provide the size."
            }else if(binding.color.text.toString().isEmpty()){
                binding.color.error = "Error:- Provide the Color."
            }else if(binding.coupanCode.text.toString().isEmpty()){
                binding.coupanCode.error = "Error:- Provide the CoupanCode."
            }
            else {
                productModel.name = binding.productName.text.toString()
                productModel.discountPrice = binding.discountprice.text.toString().toDouble()
                productModel.originalPrice = binding.originalprice.text.toString().toDouble()
                productModel.productSize = binding.size.text.toString()
                productModel.productColor = binding.color.text.toString()
                productModel.productCoupanCode = binding.coupanCode.text.toString()
                productModel.disp = binding.disp.text.toString()
                discountedPercent = calculateDiscount(
                    binding.originalprice.text.toString().toDouble(),
                    binding.discountprice.text.toString().toDouble()
                )
                binding.spinKit.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
                val formattedDiscountPercentage = String.format("%.2f", discountedPercent)
                val discountedPercentWithTwoDecimal = formattedDiscountPercentage.toDouble()
                productModel.discountPercentage = discountedPercentWithTwoDecimal


                Firebase.firestore.collection("Products").document(UUID.randomUUID().toString())
                    .set(productModel).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@MainActivity, "Product Added!!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Error in adding the product!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        binding.spinKit.visibility = View.INVISIBLE
                        binding.mainLayout.visibility = View.VISIBLE

                    }
            }

        }
    }

    private fun makeViewInvisible() {
        binding.apply {
            productImage.visibility = View.INVISIBLE
            category.visibility = View.INVISIBLE
            productName.visibility = View.INVISIBLE
            originalprice.visibility = View.INVISIBLE
            discountprice.visibility = View.INVISIBLE
            size.visibility = View.INVISIBLE
            color.visibility = View.INVISIBLE
            coupanCode.visibility = View.INVISIBLE
            disp.visibility = View.INVISIBLE
            addProduct.visibility = View.INVISIBLE

        }
    }
    private fun makeViewVisible() {
        binding.apply {
            productImage.visibility = View.VISIBLE
            category.visibility = View.VISIBLE
            productName.visibility = View.VISIBLE
            originalprice.visibility = View.VISIBLE
            discountprice.visibility = View.VISIBLE
            size.visibility = View.VISIBLE
            color.visibility = View.VISIBLE
            coupanCode.visibility = View.VISIBLE
            disp.visibility = View.VISIBLE
            addProduct.visibility = View.VISIBLE


        }
    }

    fun calculateDiscount(originalPrice: Double, discountedPrice: Double): Double {
        val discountAmount = originalPrice - discountedPrice
        val discountPercentage = (discountAmount / originalPrice) * 100
        return discountPercentage
    }
}