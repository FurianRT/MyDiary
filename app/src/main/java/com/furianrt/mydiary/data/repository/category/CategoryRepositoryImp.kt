package com.furianrt.mydiary.data.repository.category

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.*
import javax.inject.Inject

class CategoryRepositoryImp @Inject constructor(
        private val database: NoteDatabase,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : CategoryRepository {

    override fun insertCategory(category: MyCategory): Completable =
            database.categoryDao().insert(category)
                    .subscribeOn(rxScheduler)

    override fun insertCategory(categories: List<MyCategory>): Completable =
            database.categoryDao().insert(categories)
                    .subscribeOn(rxScheduler)

    override fun updateCategory(category: MyCategory): Completable =
            database.categoryDao().update(category.apply { syncWith.clear() })
                    .andThen(database.noteDao().getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapSingle { database.noteDao().update(it).toSingleDefault(true) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(rxScheduler)

    override fun updateCategoriesSync(categories: List<MyCategory>): Completable =
            database.categoryDao().update(categories)
                    .subscribeOn(rxScheduler)

    override fun deleteCategory(category: MyCategory): Completable =
            database.categoryDao().delete(category.id)
                    .subscribeOn(rxScheduler)

    override fun cleanupCategories(): Completable =
            database.categoryDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllCategories(): Flowable<List<MyCategory>> =
            database.categoryDao()
                    .getAllCategories()
                    .subscribeOn(rxScheduler)

    override fun getCategory(categoryId: String): Single<MyCategory> =
            database.categoryDao()
                    .getCategory(categoryId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedCategories(): Flowable<List<MyCategory>> =
            database.categoryDao()
                    .getDeletedCategories()
                    .subscribeOn(rxScheduler)

    override fun saveCategoriesInCloud(categories: List<MyCategory>): Completable =
            cloud.saveCategories(categories, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable =
            cloud.deleteCategories(categories, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllCategoriesFromCloud(): Single<List<MyCategory>> =
            cloud.getAllCategories(auth.getUserId())
                    .subscribeOn(rxScheduler)
}