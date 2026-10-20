package com.sourate.app

data class Category(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val sections: List<Section>
)

data class Section(
    val heading: String,
    val items: List<ContentItem>
)

data class ContentItem(
    val title: String,
    val arabic: String? = null,
    val description: String,
    val translit: String? = null,
    val imageRes: Int? = null,
    val pdfAsset: String? = null,
    val assetImage: String? = null
)

data class Surah(
    val n: Int,
    val name: String,
    val ar: String,
    val meaning: String,
    val type: String,
    val count: Int,
    val ayahs: List<Ayah>
)

data class Ayah(
    val i: Int,
    val ar: String,
    val tr: String,
    val fr: String
)

data class XassidaMeta(
    val slug: String,
    val title: String
)

data class XassidaVerse(
    val translit: String,
    val french: String
)

data class XassidaDetail(
    val slug: String,
    val title: String,
    val audioUrls: List<String> = emptyList(),
    val arabicImages: List<String> = emptyList(),
    val verses: List<XassidaVerse> = emptyList()
)
