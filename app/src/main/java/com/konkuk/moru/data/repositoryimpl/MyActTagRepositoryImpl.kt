package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.service.MyActTagService
import com.konkuk.moru.domain.model.MyActTag
import com.konkuk.moru.domain.repository.MyActTagRepository
import javax.inject.Inject
import com.konkuk.moru.data.mapper.toDomain

class MyActTagRepositoryImpl @Inject constructor(
    private val service: MyActTagService
) : MyActTagRepository {

    override suspend fun getAllTags(): List<MyActTag> =
        service.getAllTags().map { it.toDomain() }

    override suspend fun getMyFavoriteTags(): List<MyActTag> =
        service.getFavoriteTags().map { it.toDomain() }
}