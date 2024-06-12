package com.example.login

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Produtos (
    @SerializedName("PRODUTO_ID") val produtoId: Int,
    @SerializedName("PRODUTO_NOME") val produtoNome: String,
    @SerializedName("PRODUTO_DESC") val produtoDesc: String?,
    @SerializedName("PRODUTO_PRECO") val produtoPreco: Double,
    @SerializedName("PRODUTO_DESCONTO") val produtoDesconto: String?,
    @SerializedName("CATEGORIA_ID") val categoriaId: Int,
    @SerializedName("PRODUTO_ATIVO") val produtoAtivo: Int,
    @SerializedName("IMAGEM_URL") val imagemUrl: String?,
    @SerializedName("QUANTIDADE_DISPONIVEL") val quantidadeDisponivel: Int




): Parcelable


