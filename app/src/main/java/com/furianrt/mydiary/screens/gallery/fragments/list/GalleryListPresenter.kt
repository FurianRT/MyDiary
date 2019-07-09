package com.furianrt.mydiary.screens.gallery.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import javax.inject.Inject

class GalleryListPresenter @Inject constructor(
        private val dataManager: DataManager
) : GalleryListContract.Presenter() {

    private lateinit var mNoteId: String
    private var mSelectedImageNames = HashSet<String>()

    override fun onListItemClick(image: MyImage, position: Int, selectionActive: Boolean) {
        if (selectionActive) {
            selectListItem(image.name)
        } else {
            view?.showViewImagePager(image.noteId, position)
        }
    }

    private fun selectListItem(imageName: String) {
        if (mSelectedImageNames.contains(imageName)) {
            mSelectedImageNames.remove(imageName)
            view?.deselectImage(imageName)
        } else {
            mSelectedImageNames.add(imageName)
            view?.selectImage(imageName)
        }
        view?.showSelectedImageCount(mSelectedImageNames.size)
    }

    override fun setNoteId(noteId: String) {
        mNoteId = noteId
    }

    override fun onViewStart() {
        loadImages(mNoteId)
    }

    private fun loadImages(noteId: String) {
        addDisposable(dataManager.getImagesForNote(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    view?.showSelectedImageCount(mSelectedImageNames.size)
                    if (images.isEmpty()) {
                        view?.closeCab()
                        view?.showEmptyList()
                    } else {
                        var i = 0
                        view?.showImages(images
                                .sortedWith(compareBy(MyImage::order, MyImage::addedTime))
                                .apply { forEach { it.order = i++ } }, mSelectedImageNames)
                    }
                })
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        addDisposable(dataManager.updateImage(images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onButtonMultiSelectionClick() {
        view?.activateSelection()
        view?.showSelectedImageCount(mSelectedImageNames.size)
    }

    override fun onButtonCabDeleteClick() {
        if (mSelectedImageNames.isEmpty()) {
            view?.closeCab()
        } else {
            view?.showDeleteConfirmationDialog(mSelectedImageNames.toList())
        }
    }

    override fun onButtonDeleteConfirmClick() {
        mSelectedImageNames.clear()
        view?.closeCab()
    }

    override fun onButtonCabSelectAllClick() {
        addDisposable(dataManager.getImagesForNote(mNoteId)
                .first(emptyList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    mSelectedImageNames.clear()
                    mSelectedImageNames.addAll(images.map { it.name })
                    view?.showSelectedImageCount(mSelectedImageNames.size)
                    view?.selectImages(mSelectedImageNames)
                })
    }

    override fun onRestoreInstanceState(selectedImageNames: Set<String>?) {
        selectedImageNames?.let {
            mSelectedImageNames.clear()
            mSelectedImageNames.addAll(it)
        }
    }

    override fun onCabCloseSelection() {
        mSelectedImageNames.clear()
        view?.showSelectedImageCount(mSelectedImageNames.size)
        view?.deactivateSelection()
    }

    override fun onSaveInstanceState() = mSelectedImageNames

    override fun onButtonAddImageClick() {
        view?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = mNoteId + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNoteId, DateTime.now().millis)
                }
                .flatMapSingle { image -> dataManager.saveImageToStorage(image) }
                .flatMapSingle { savedImage ->
                    dataManager.insertImage(savedImage).toSingleDefault(true)
                }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.hideLoading() })
    }

    override fun onImageTrashed(image: MyImage) {
        view?.showDeleteConfirmationDialog(listOf(image.name))
    }
}