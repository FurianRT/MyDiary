package com.furianrt.mydiary.authentication.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.authentication.AuthFragment
import kotlinx.android.synthetic.main.fragment_registration.view.*
import javax.inject.Inject

class RegistrationFragment : Fragment(), RegistrationContract.View {

    companion object {
        const val TAG = "RegistrationFragment"
    }

    @Inject
    lateinit var mPresenter: RegistrationContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_registration, container, false)

        view.button_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }

        return view
    }

    override fun close() {
        fragmentManager?.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        (parentFragment as? AuthFragment?)?.onRegistrationFragmentDetach()
    }
}
