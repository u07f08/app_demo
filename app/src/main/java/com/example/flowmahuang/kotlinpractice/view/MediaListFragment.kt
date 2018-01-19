package com.example.flowmahuang.kotlinpractice.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.flowmahuang.kotlinpractice.R
import com.example.flowmahuang.kotlinpractice.controller.MediaListController

class MediaListFragment : Fragment() {
    private lateinit var mHeaderSelectRelativeLayout: RelativeLayout
    private lateinit var mHeaderSelectText: TextView
    private lateinit var mHeaderSelectImage: ImageView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPermissionDenyText:TextView

    private lateinit var mController: MediaListController

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater!!.inflate(R.layout.fragment_media_list, container, false)

        mRecyclerView = v.findViewById(R.id.media_list_recycler_view)
        mHeaderSelectText = v.findViewById(R.id.media_select_header_select_text)
        mHeaderSelectImage = v.findViewById(R.id.media_select_header_select_image)
        mHeaderSelectRelativeLayout = v.findViewById(R.id.media_list_header)
        mPermissionDenyText = v.findViewById(R.id.media_select_permission_deny_text)

        mController = MediaListController(activity, controllerCallback)
        mController.getMediaUriArray(mRecyclerView)
        mHeaderSelectRelativeLayout.setOnClickListener(clickListener)

        return v
    }

    private fun setHeaderSelectImage(isSelect: Boolean) {
        if (isSelect) {
            mHeaderSelectImage.setImageResource(R.drawable.check)
            mHeaderSelectText.setText(R.string.file_page_unselected_all)
        } else {
            mHeaderSelectImage.setImageBitmap(null)
            mHeaderSelectText.setText(R.string.file_page_select_all)
        }
    }

    /**
     *  Callback Func
     */
    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.media_list_update_button -> {
                //TODO: do something
            }
            R.id.media_list_header -> {
                val mHeaderSelect = mController.setAdapterHeader()
                setHeaderSelectImage(mHeaderSelect)
            }

        }
    }

    private val controllerCallback = object : MediaListController.MediaListControllerCallback {
        override fun isSelectAll(isSelect: Boolean) {
            setHeaderSelectImage(isSelect)
        }

        override fun isPermissionDeny() {
            mHeaderSelectRelativeLayout.visibility = View.INVISIBLE
            mPermissionDenyText.visibility = View.VISIBLE
        }
    }
}
