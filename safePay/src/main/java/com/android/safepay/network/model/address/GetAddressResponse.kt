package com.android.safepay.network.model.address

import com.google.gson.annotations.SerializedName

data class GetAddressResponse(
    @SerializedName("api_version") val apiVersion: String,
    @SerializedName("data") val data: Data
)

data class Data(
    @SerializedName("required") val requiredFields: List<String>,
    @SerializedName("AdministrativeArea") val administrativeArea: AdministrativeArea,
    @SerializedName("Locality") val locality: NameField,
    @SerializedName("StreetAddress") val streetAddress: NameField,
    @SerializedName("PostCode") val postCode: NameField
)

data class AdministrativeArea(
    @SerializedName("name") val name: String,
    @SerializedName("options") val options: List<Option>
)

data class Option(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class NameField(
    @SerializedName("name") val name: String

)
