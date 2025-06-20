package com.mercu.intrain.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.mercu.intrain.R
import com.mercu.intrain.API.CVReviewHistoryResponse
import com.mercu.intrain.API.ReviewSection
import com.google.gson.Gson

class CVReviewDetailDialogFragment : BottomSheetDialogFragment() {
    private lateinit var fileNameText: TextView
    private lateinit var fileTypeChip: Chip
    private lateinit var uploadedAtText: TextView
    private lateinit var overallFeedbackText: TextView
    private lateinit var sectionFeedbackRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_cv_review_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fileNameText = view.findViewById<TextView>(R.id.cvFileNameText)
        val fileTypeChip = view.findViewById<Chip>(R.id.fileTypeChip)
        val uploadedAtText = view.findViewById<TextView>(R.id.uploadedAtText)
        val overallFeedbackText = view.findViewById<TextView>(R.id.overallFeedbackText)
        val sectionFeedbackRecyclerView = view.findViewById<RecyclerView>(R.id.sectionFeedbackRecyclerView)

        val json = arguments?.getString(ARG_CV_REVIEW)
        val review = Gson().fromJson(json, com.mercu.intrain.API.CVReviewHistoryResponse::class.java)
        fileNameText?.text = review.submission.fileName
        fileTypeChip?.text = review.submission.fileType.uppercase()
        uploadedAtText?.text = "Uploaded: ${review.submission.uploadedAt.take(10)}"
        overallFeedbackText?.text = review.review?.overallFeedback ?: "No feedback yet."
        sectionFeedbackRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        sectionFeedbackRecyclerView?.adapter = SectionFeedbackAdapter(review.sections)
    }

    companion object {
        private const val ARG_CV_REVIEW = "cv_review"
        fun newInstance(review: com.mercu.intrain.API.CVReviewHistoryResponse): CVReviewDetailDialogFragment {
            val fragment = CVReviewDetailDialogFragment()
            val args = Bundle()
            args.putString(ARG_CV_REVIEW, Gson().toJson(review))
            fragment.arguments = args
            return fragment
        }
    }
}

class SectionFeedbackAdapter(private val items: List<com.mercu.intrain.API.ReviewSection>) : RecyclerView.Adapter<SectionFeedbackAdapter.SectionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section_feedback, parent, false)
        return SectionViewHolder(view)
    }
    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int = items.size
    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: com.mercu.intrain.API.ReviewSection) {
            val sectionNameText = itemView.findViewById<TextView>(R.id.sectionNameText)
            val sectionFeedbackText = itemView.findViewById<TextView>(R.id.sectionFeedbackText)
            val needsImprovementChip = itemView.findViewById<Chip>(R.id.needsImprovementChip)
            sectionNameText?.text = data.section.replaceFirstChar { it.uppercase() }
            sectionFeedbackText?.text = data.feedback
            needsImprovementChip?.visibility = if (data.needsImprovement) View.VISIBLE else View.GONE
        }
    }
} 