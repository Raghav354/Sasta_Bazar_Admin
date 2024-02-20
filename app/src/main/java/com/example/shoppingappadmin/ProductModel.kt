package com.example.shoppingappadmin

class ProductModel {
    var name: String = ""
    var discountPrice: Double = -1.0
    var originalPrice:Double = -1.0
    var disp: String = ""
    var imageUrl: String = ""
    var category: String = ""
    var productCoupanCode = ""
    var productSize = ""
    var productColor = ""
    var discountPercentage:Double? = null

    constructor(name: String, price: Double, disp: String, imageUrl: String) {
        this.name = name
        this.discountPrice = price
        this.disp = disp
        this.imageUrl = imageUrl
    }

    constructor()

    constructor(name: String, price: Double, disp: String, imageUrl: String, category: String) {
        this.name = name
        this.discountPrice = price
        this.disp = disp
        this.imageUrl = imageUrl
        this.category = category
    }

    constructor(
        name: String,
        discountPrice: Double,
        originalPrice: Double,
        disp: String,
        imageUrl: String,
        category: String,
        productCoupanCode: String,
        productSize: String,
        productColor: String,
        discountPercentage: Double?
    ) {
        this.name = name
        this.discountPrice = discountPrice
        this.originalPrice = originalPrice
        this.disp = disp
        this.imageUrl = imageUrl
        this.category = category
        this.productCoupanCode = productCoupanCode
        this.productSize = productSize
        this.productColor = productColor
        this.discountPercentage = discountPercentage
    }
}