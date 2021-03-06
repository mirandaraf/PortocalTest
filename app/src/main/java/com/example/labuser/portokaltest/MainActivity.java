package com.example.labuser.portokaltest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ArticleAdapter(this,
                0);

        ListView listView = (ListView) findViewById(R.id.article_list);
        listView.setAdapter(adapter);
        GetArticlesTask task = new GetArticlesTask();
        task.execute();
    }

    private class GetArticlesTask extends AsyncTask<Void, Void, Article[]> {

        protected Article[] doInBackground(Void... voids) {
            String articleJsonStr = null;

            // Get JSON from REST API

            HttpURLConnection urlConnection = null;

            try {
                final String baseUrl = "https://newsapi.org/v1/articles?source=mashable&sortBy=top&apiKey=9571ef6f12e84e0eb6302152de2677e7";

                // Get input stream

                URL url = new URL(baseUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                // Extract JSON string from input stream


                StringBuilder out = new StringBuilder();
                InputStreamReader in = new InputStreamReader(inputStream, "UTF-8");

                char[] buffer = new char[1024];
                int rsz;
                while ((rsz = in.read(buffer, 0, buffer.length)) >= 0) {
                    out.append(buffer, 0, rsz);
                }

                articleJsonStr = out.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            // Turn JSON to Array of Strings

            try {
                JSONObject articlesJson = new JSONObject(articleJsonStr);
                JSONArray articleJsonArray = articlesJson.getJSONArray("articles");

                Article[] articlesArray = new Article[articleJsonArray.length()];

                for (int i = 0; i < articleJsonArray.length(); i++) {
                    JSONObject articleJson = (JSONObject) articleJsonArray.get(i);

                    Article article = new Article();
                    article.setTitle(articleJson.getString("title"));
                    article.setDescription(articleJson.getString("description"));
                    article.setAuthor(articleJson.getString("author"));
                    articlesArray[i] = article;
                }

                return articlesArray;
            } catch (JSONException e) {
                return null;
            }
        }

        protected void onPostExecute(Article[] articles) {
            adapter.clear();

            adapter.addAll(articles);

        }
    }


}
