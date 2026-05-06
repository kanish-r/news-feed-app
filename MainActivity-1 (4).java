package com.example.newsfeed;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * MAD Lab Program — News Feed App
 * ─────────────────────────────────────────────────────────────────
 * Aim  : Build a News Feed Android Application with:
 *        • Two card types — Featured (large) + Regular (compact)
 *        • Category filter tabs (All / Tech / Sports / Science / Business)
 *        • Live search by headline or source
 *        • Bookmark Toast on 🔖 tap
 *        • Card tap → Toast with full article summary
 *        • Today's date displayed in header
 *
 * Files:
 *   MainActivity.java          → Main logic, filter, search
 *   NewsAdapter.java           → RecyclerView with 2 ViewTypes
 *   NewsArticle.java           → Data model
 *   activity_main.xml          → Main layout
 *   item_news_featured.xml     → Large featured card
 *   item_news_regular.xml      → Compact horizontal card
 * ─────────────────────────────────────────────────────────────────
 */
public class MainActivity extends AppCompatActivity {

    // ── UI ────────────────────────────────────────────────────────
    private RecyclerView recyclerView;
    private NewsAdapter  adapter;
    private EditText     etSearch;
    private TextView     tvDate, tvArticleCount;
    private TextView     tabAll, tabTech, tabSports, tabScience, tabBusiness;

    // ── Data ──────────────────────────────────────────────────────
    private List<NewsArticle> allArticles    = new ArrayList<>();
    private List<NewsArticle> filteredList   = new ArrayList<>();
    private String            activeCategory = "All";

    // Category → color map
    private static final int COLOR_TECH     = 0xFF0277BD; // Blue
    private static final int COLOR_SPORTS   = 0xFF2E7D32; // Green
    private static final int COLOR_SCIENCE  = 0xFF6A1B9A; // Purple
    private static final int COLOR_BUSINESS = 0xFFBF360C; // Deep Orange
    private static final int COLOR_FEATURED = 0xFFB71C1C; // Dark Red

    // ─── onCreate ─────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        recyclerView    = findViewById(R.id.recyclerView);
        etSearch        = findViewById(R.id.etSearch);
        tvDate          = findViewById(R.id.tvDate);
        tvArticleCount  = findViewById(R.id.tvArticleCount);
        tabAll          = findViewById(R.id.tabAll);
        tabTech         = findViewById(R.id.tabTech);
        tabSports       = findViewById(R.id.tabSports);
        tabScience      = findViewById(R.id.tabScience);
        tabBusiness     = findViewById(R.id.tabBusiness);

        // Show today's date
        String today = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                           .format(new Date());
        tvDate.setText(today);

        // Load articles
        loadArticles();
        filteredList.addAll(allArticles);

        // Setup RecyclerView
        adapter = new NewsAdapter(this, filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        updateCount();

        // Category tab listeners
        tabAll     .setOnClickListener(v -> selectTab("All",      tabAll));
        tabTech    .setOnClickListener(v -> selectTab("Tech",     tabTech));
        tabSports  .setOnClickListener(v -> selectTab("Sports",   tabSports));
        tabScience .setOnClickListener(v -> selectTab("Science",  tabScience));
        tabBusiness.setOnClickListener(v -> selectTab("Business", tabBusiness));

        // Live search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArticles(activeCategory, s.toString().trim());
            }
        });
    }

    // ══════════════════════════════════════════════════════════════
    //  Load Articles (Static news data — 12 articles)
    // ══════════════════════════════════════════════════════════════
    private void loadArticles() {

        // ── FEATURED headline ─────────────────────────────────────
        allArticles.add(new NewsArticle(
            "India Launches Next-Gen AI Supercomputer",
            "The Indian government unveiled a 100 petaflop AI supercomputer today, " +
            "marking a major milestone in the country's push for technology self-reliance " +
            "and positioning India among the world's top 5 computing powers.",
            "Tech", "Times of India", "1 hr ago",
            "🖥️", "6 min read", COLOR_FEATURED, true));  // ← isFeatured = true

        // ── Tech Articles ─────────────────────────────────────────
        allArticles.add(new NewsArticle(
            "Android 16 Release Date Confirmed by Google",
            "Google officially announced Android 16 will launch in Q2 2026 with " +
            "major UI overhaul, better battery management, and satellite messaging support.",
            "Tech", "Android Authority", "3 hrs ago",
            "💻", "4 min read", COLOR_TECH, false));

        allArticles.add(new NewsArticle(
            "5G Coverage Reaches 700 Indian Cities",
            "Telecom operators report 5G network expansion now covers over 700 cities " +
            "across India, with rural deployment plans underway for 2026.",
            "Tech", "Economic Times", "5 hrs ago",
            "📡", "3 min read", COLOR_TECH, false));

        allArticles.add(new NewsArticle(
            "ChatGPT Surpasses 500 Million Active Users",
            "OpenAI reported that ChatGPT has crossed 500 million monthly active users, " +
            "cementing its position as the world's most popular AI assistant.",
            "Tech", "TechCrunch", "7 hrs ago",
            "🤖", "3 min read", COLOR_TECH, false));

        // ── Sports Articles ───────────────────────────────────────
        allArticles.add(new NewsArticle(
            "India Beat Australia by 6 Wickets in 3rd ODI",
            "Shubman Gill's brilliant century steered India to a commanding 6-wicket " +
            "victory in the third ODI, completing a series sweep against Australia in Mumbai.",
            "Sports", "Cricinfo", "2 hrs ago",
            "🏏", "4 min read", COLOR_SPORTS, false));

        allArticles.add(new NewsArticle(
            "Neeraj Chopra Sets New National Javelin Record",
            "Olympic champion Neeraj Chopra shattered his own national record with a " +
            "throw of 91.2 metres at the Lausanne Diamond League meet.",
            "Sports", "Sportstar", "4 hrs ago",
            "🏅", "2 min read", COLOR_SPORTS, false));

        allArticles.add(new NewsArticle(
            "IPL 2026 Season to Begin on March 22",
            "BCCI confirmed the IPL 2026 season will kick off on March 22 in Chennai, " +
            "with 10 teams competing for the title over 74 league matches.",
            "Sports", "BCCI Official", "6 hrs ago",
            "🏟️", "3 min read", COLOR_SPORTS, false));

        // ── Science Articles ──────────────────────────────────────
        allArticles.add(new NewsArticle(
            "NASA Confirms Water Ice Found on Mars Surface",
            "NASA's Perseverance rover has confirmed the presence of accessible water ice " +
            "deposits near the Martian equator, significantly boosting future mission plans.",
            "Science", "NASA News", "3 hrs ago",
            "🔬", "5 min read", COLOR_SCIENCE, false));

        allArticles.add(new NewsArticle(
            "Scientists Develop Biodegradable Plastic from Seaweed",
            "Researchers at IIT Madras have developed a fully biodegradable plastic " +
            "substitute using seaweed extracts, which decomposes in just 30 days.",
            "Science", "Nature Journal", "8 hrs ago",
            "🧪", "4 min read", COLOR_SCIENCE, false));

        // ── Business Articles ─────────────────────────────────────
        allArticles.add(new NewsArticle(
            "Sensex Hits All-Time High of 82,000 Points",
            "The BSE Sensex surged to a historic 82,000 points mark driven by strong " +
            "FII inflows and positive Q4 earnings from banking and IT sectors.",
            "Business", "Business Standard", "1 hr ago",
            "📈", "3 min read", COLOR_BUSINESS, false));

        allArticles.add(new NewsArticle(
            "Tata Motors to Launch 5 New EVs in 2026",
            "Tata Motors announced an aggressive EV rollout plan for 2026, including " +
            "three SUVs and two sedans, targeting a 30% market share in the EV segment.",
            "Business", "Mint", "4 hrs ago",
            "🚗", "3 min read", COLOR_BUSINESS, false));

        allArticles.add(new NewsArticle(
            "RBI Keeps Repo Rate Unchanged at 6.25%",
            "The Reserve Bank of India's Monetary Policy Committee unanimously voted to " +
            "keep the repo rate unchanged at 6.25%, citing stable inflation and growth.",
            "Business", "Financial Express", "6 hrs ago",
            "🏦", "4 min read", COLOR_BUSINESS, false));
    }

    // ══════════════════════════════════════════════════════════════
    //  Category Tab Selection
    // ══════════════════════════════════════════════════════════════
    private void selectTab(String category, TextView selectedTab) {
        activeCategory = category;

        // Reset all tabs
        TextView[] tabs = {tabAll, tabTech, tabSports, tabScience, tabBusiness};
        for (TextView tab : tabs) {
            tab.setTextColor(Color.parseColor("#FFCDD2"));
            tab.setBackground(null);
        }

        // Highlight selected tab
        selectedTab.setTextColor(Color.WHITE);
        selectedTab.setBackgroundColor(Color.parseColor("#D32F2F"));

        filterArticles(category, etSearch.getText().toString().trim());
    }

    // ══════════════════════════════════════════════════════════════
    //  Filter by Category + Search keyword
    // ══════════════════════════════════════════════════════════════
    private void filterArticles(String category, String keyword) {
        filteredList.clear();
        for (NewsArticle article : allArticles) {
            boolean matchCategory = category.equals("All") ||
                                    article.getCategory().equals(category);
            boolean matchKeyword  = keyword.isEmpty() ||
                                    article.getTitle().toLowerCase()
                                           .contains(keyword.toLowerCase()) ||
                                    article.getSource().toLowerCase()
                                           .contains(keyword.toLowerCase());
            if (matchCategory && matchKeyword) {
                filteredList.add(article);
            }
        }
        adapter.updateList(filteredList);
        updateCount();
    }

    // ── Update article count label ─────────────────────────────────
    private void updateCount() {
        int n = filteredList.size();
        tvArticleCount.setText(n + " article" + (n != 1 ? "s" : "") + " found");
    }
}
