/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.util.ArrayList;

import org.gots.R;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.analytics.GotsAnalytics;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.GrowingSeedDBHelper;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ActionActivity extends Activity implements OnClickListener {

	private ListAllActionAdapter listActions;
	ListView listAllotments;
	ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		setContentView(R.layout.actions);
		int seedid = 0;

		GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);

		if (getIntent().getExtras() != null)
			seedid = getIntent().getExtras().getInt("org.gots.seed.id");

		if (seedid > 0) {
			allSeeds.add(helper.getSeedById(seedid));
		} else
			allSeeds = helper.getGrowingSeeds();
		listActions = new ListAllActionAdapter(this, allSeeds, ListAllActionAdapter.STATUS_TODO);
		listAllotments = (ListView) findViewById(R.id.IdGardenActionsList);
		listAllotments.setAdapter(listActions);
		listAllotments.setDivider(null);
		listAllotments.setDividerHeight(0);

		LinearLayout dashboardButton = (LinearLayout) findViewById(R.id.btReturn);
		dashboardButton.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// GardenFactory gf = new GardenFactory(this);
		// gf.saveGarden(DashboardActivity.myGarden);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.menu_action, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		// case R.id.new_action:
		//
		// Intent i = new Intent(this, NewActionActivity.class);
		// startActivity(i);
		//
		// return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		listActions.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		listActions.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}
}
