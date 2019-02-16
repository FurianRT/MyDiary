package com.furianrt.mydiary.authentication.done

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R

class DoneAuthFragment : Fragment() {

    companion object {
        const val TAG = "DoneAuthFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_done_auth, container, false)
    }
}
