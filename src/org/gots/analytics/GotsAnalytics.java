package org.gots.analytics;

import org.gots.preferences.GotsPreferences;

import android.app.Application;
import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GotsAnalytics {

	protected static GotsAnalytics INSTANCE;

	protected int activityCount = 0;
	protected Integer dispatchIntervalSecs;
	protected static String apiKey = GotsPreferences.ANALYTICS_ID;
	protected Context context;

	/**
	 * NOTE: you should use your Application context, not your Activity context,
	 * in order to avoid memory leaks.
	 */
	protected GotsAnalytics(Application context) {
		this.apiKey = apiKey;
		this.context = context;
	}

	/**
	 * NOTE: you should use your Application context, not your Activity context,
	 * in order to avoid memory leaks.
	 */
	protected GotsAnalytics(int dispatchIntervalSecs, Application context) {
		this.dispatchIntervalSecs = dispatchIntervalSecs;
		this.context = context;
	}

	/**
	 * This should be called once in onCreate() for each of your activities that
	 * use GoogleAnalytics. These methods are not synchronized and don't
	 * generally need to be, so if you want to do anything unusual you should
	 * synchronize them yourself.
	 */
	public void incrementActivityCount() {
		if (activityCount == 0) {
			if (dispatchIntervalSecs == null)
				GoogleAnalyticsTracker.getInstance().startNewSession(apiKey, context);
			else
				GoogleAnalyticsTracker.getInstance().startNewSession(apiKey, dispatchIntervalSecs, context);
			
			if (GotsPreferences.DEVELOPPEMENT)
				GoogleAnalyticsTracker.getInstance().setDryRun(true);
		}
		++activityCount;
	}

	/**
	 * This should be called once in onDestrkg() for each of your activities
	 * that use GoogleAnalytics. These methods are not synchronized and don't
	 * generally need to be, so if you want to do anything unusual you should
	 * synchronize them yourself.
	 */
	public void decrementActivityCount() {
		activityCount = Math.max(activityCount - 1, 0);

		if (activityCount == 0)
			GoogleAnalyticsTracker.getInstance().stopSession();
	}

	/**
	 * Get or create an instance of GoogleAnalyticsSessionManager
	 */
	public static GotsAnalytics getInstance(Application application) {
		if (INSTANCE == null)
			INSTANCE = new GotsAnalytics(application);
		return INSTANCE;
	}

}
