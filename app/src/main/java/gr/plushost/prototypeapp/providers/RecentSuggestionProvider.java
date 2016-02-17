package gr.plushost.prototypeapp.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by billiout on 3/3/2015.
 */
public class RecentSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "RecentSuggestionProvider.plushost.gr";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
