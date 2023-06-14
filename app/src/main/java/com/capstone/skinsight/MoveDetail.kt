package com.capstone.skinsight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MoveDetail : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move_detail)

        val dataClubs = intent.getParcelableExtra<Article>("DATA")

        val tvDetailName : TextView = findViewById(R.id.tvTitle)
        val tvDetailDescription : TextView = findViewById(R.id.tvDesc)
        val ivDetailPhoto : ImageView = findViewById(R.id.ivArticle)
//        val tvDetailManager : TextView = findViewById(R.id.manager)
//        val tvDetailNickname: TextView = findViewById(R.id.nickname)

        tvDetailName.text = dataClubs?.title
        tvDetailDescription.text = dataClubs?.description
        dataClubs?.photo?.let { ivDetailPhoto.setImageResource(it) }

//        val btnDialPhone: Button = findViewById(R.id.btn_share)
//        btnDialPhone.setOnClickListener(this)

    }
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.btn_share -> {
//                val tvDetailDescription : TextView = findViewById(R.id.tv_item_description)
//                val sendIntent: Intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, tvDetailDescription.text)
//                    type = "text/plain"
//                }
//                val shareIntent = Intent.createChooser(sendIntent, null)
//                startActivity(shareIntent)
//            }
//        }
//    }
}