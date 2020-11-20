package com.mbds.newsletter.data.adapters

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mbds.newsletter.FavDB
import com.mbds.newsletter.R
import com.mbds.newsletter.models.Article
import com.mbds.newsletter.models.ArticleQuery
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(
    item: Article, private val handler: ArticleHandler, val context: Context
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private val mArticle: Article = item
    private lateinit var favDB: FavDB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        favDB = FavDB(context);
        //create table on first
        val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)

        if (firstStart) {
            createTableOnFirstStart()
        }

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.detailed_article_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article: Article = mArticle
        val context = holder.itemView.context

        article.favorite = "0"
        //article.id = position.toString()

        readCursorData(article, holder)
        //Conversion de la date
        val sdfOut = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = article.publishedAt
        val dateString = sdfOut.format(date)

        // Display Neighbour Name
        if (article.author.isNullOrBlank()) holder.mArticleAuthor.visibility = GONE else holder.mArticleAuthor.text = article.author
        holder.mArticleName.text = article.title
        holder.mArticleDate.text = dateString
        holder.mArticleContent.text = article.content

        Glide.with(context)
            .load(article.urlToImage)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_image_24)
            .skipMemoryCache(false)
            .into(holder.mArticleAvatar)

        // Initialisation button fav
        if (article.favorite == "0") holder.mFavoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        else holder.mFavoriteButton.setImageResource(
            R.drawable.ic_favorite_red_24dp
        )

        //add to fav btn
        holder.mFavoriteButton.setOnClickListener(View.OnClickListener {

            if (article.favorite == "0") {
                article.favorite = "1"
                favDB.insertIntoTheDatabase(
                    article.id,
                    article.title,
                    article.description,
                    article.author,
                    article.urlToImage,
                    //dateString,
                    article.favorite
                )
                holder.mFavoriteButton.setImageResource(R.drawable.ic_favorite_red_24dp)
            } else {
                article.favorite = "0"
                favDB.remove_fav(article.id)
                holder.mFavoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        })
    }

    override fun getItemCount(): Int {
        return 1
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val mArticleAvatar: ImageView
        val mArticleName: TextView
        val mArticleAuthor: TextView
        val mArticleDate: TextView
        val mArticleContent: TextView
        val mFavoriteButton: ImageView
        init {
            // Enable click on item
            mArticleAvatar = view.findViewById(R.id.article_urlToImage)
            mArticleName = view.findViewById(R.id.article_title)
            mArticleAuthor = view.findViewById(R.id.article_author)
            mArticleDate = view.findViewById(R.id.article_publishedAt)
            mArticleContent = view.findViewById(R.id.article_content)
            mFavoriteButton = view.findViewById(R.id.article_favorite)
        }
    }

    private fun createTableOnFirstStart() {
        val prefs =
            context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()
    }

    private fun readCursorData(
        article: Article,
        viewHolder: ViewHolder
    ) {
        val cursor = favDB.read_all_data(article.id)
        val db = favDB.readableDatabase
        try {
            while (cursor.moveToNext()) {
                val item_fav_status =
                    cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS))
                article.favorite = item_fav_status

                //check fav status
                if (item_fav_status != null && item_fav_status == "1") {
                    viewHolder.mFavoriteButton.setImageResource(R.drawable.ic_favorite_red_24dp)
                } else if (item_fav_status != null && item_fav_status == "0") {
                    viewHolder.mFavoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed) cursor.close()
            db.close()
        }
    }
}