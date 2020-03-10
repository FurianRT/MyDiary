/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.image

import android.graphics.Bitmap
import com.furianrt.mydiary.model.entity.MyHeaderImage
import com.furianrt.mydiary.model.entity.MyImage
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ImageGateway {
    fun insertImage(image: MyImage): Completable
    fun insertImages(images: List<MyImage>): Completable
    fun insertHeaderImage(headerImage: MyHeaderImage): Completable
    fun updateImage(image: MyImage): Completable
    fun updateImage(images: List<MyImage>): Completable
    fun updateImageSync(images: List<MyImage>): Completable
    fun deleteImage(imageName: String): Completable
    fun deleteImage(imageNames: List<String>): Completable
    fun deleteImageFromStorage(fileName: String): Single<Boolean>
    fun deleteImagesFromCloud(images: List<MyImage>): Completable
    fun cleanupImages(): Completable
    fun getAllImages(): Flowable<List<MyImage>>
    fun getDeletedImages(): Flowable<List<MyImage>>
    fun getDeletedImages(noteId: String): Flowable<List<MyImage>>
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
    fun getImageCount(): Flowable<Int>
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
    fun getAllImagesFromCloud(): Single<List<MyImage>>
    fun loadHeaderImages(category: String, page: Int = 1, perPage: Int = 20): Single<List<MyHeaderImage>>
    fun saveImageToStorage(image: MyImage): Single<MyImage>
    fun saveBitmapToStorage(bitmap: Bitmap, image: MyImage): Single<MyImage>
    fun saveImagesInCloud(images: List<MyImage>): Completable
    fun saveImagesFilesInCloud(images: List<MyImage>): Completable
    fun loadImageFiles(images: List<MyImage>): Completable
    fun isDailyImageEnabled(): Boolean
    fun getDailyImageCategory(): String
    fun getAvailableImageDirectory(): String
    fun isPanoramaEnabled(): Boolean
}