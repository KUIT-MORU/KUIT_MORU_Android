package com.konkuk.moru.domain.repository

import com.konkuk.moru.domain.model.MyActTag

interface MyActTagRepository {
    suspend fun getAllTags(): List<MyActTag>

    suspend fun getMyFavoriteTags(): List<MyActTag>
}