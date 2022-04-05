package com.abdulaziz.youtube.models.searchbytext

import java.io.Serializable

data class Item(
    val etag: String,
    val id: Id,
    val kind: String,
    val snippet: Snippet
):Serializable