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
package org.gots.seed.view;

import java.util.ArrayList;
import java.util.Iterator;

import net.londatiga.android.QuickAction;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.DetailAction;
import org.gots.action.bean.PlanAction;
import org.gots.action.bean.WateringAction;
import org.gots.action.sql.ActionDBHelper;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.action.view.ActionWidget;
import org.gots.seed.GrowingSeedInterface;
import org.gots.ui.NewActionActivity;
import org.gots.ui.TabSeedActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class QuickSeedActionBuilder {

	final QuickAction quickAction;
	private View parentView;

	public QuickSeedActionBuilder(final SeedWidget v, final BaseAdapter parentAdapter) {
		parentView = v;
		final GrowingSeedInterface seed = (GrowingSeedInterface) v.getTag();

		ActionSeedDBHelper helperActions = new ActionSeedDBHelper(v.getContext());
		ArrayList<BaseActionInterface> actions = helperActions.getActionsToDoBySeed(seed);

		quickAction = new QuickAction(v.getContext(), QuickAction.HORIZONTAL);

		for (Iterator<BaseActionInterface> iterator = actions.iterator(); iterator.hasNext();) {
			BaseActionInterface baseActionInterface = iterator.next();
			if (!SeedActionInterface.class.isInstance(baseActionInterface))
				break;
			final SeedActionInterface currentAction = (SeedActionInterface) baseActionInterface;

			ActionWidget actionWidget = new ActionWidget(v.getContext(), currentAction);

			if (currentAction == null)
				continue;

			quickAction.addActionItem(actionWidget);
			actionWidget.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SeedActionInterface actionItem = (SeedActionInterface) currentAction;
					actionItem.execute(seed);
					parentAdapter.notifyDataSetChanged();
					quickAction.dismiss();
				}
			});
			// actionWidget.setOnActionItemClickListener(new
			// ActionWidget.OnActionItemClickListener() {
			// @Override
			// public void onItemClick(ActionWidget source, BaseActionInterface
			// action) {
			// SeedActionInterface actionItem = (SeedActionInterface) action;
			// actionItem.execute(seed);
			// parentAdapter.notifyDataSetChanged();
			// quickAction.dismiss();
			// }
			// });
		}

		PlanAction planAction = new PlanAction(v.getContext());
		ActionWidget actionWidget = new ActionWidget(v.getContext(), planAction);
		actionWidget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), NewActionActivity.class);
				i.putExtra("org.gots.seed.id", seed.getGrowingSeedId());
				v.getContext().startActivity(i);
				quickAction.dismiss();
			}
		});
		// actionWidget.setOnActionItemClickListener(new
		// ActionWidget.OnActionItemClickListener() {
		// @Override
		// public void onItemClick(ActionWidget source, BaseActionInterface
		// action) {
		//
		// }
		// });
		quickAction.addActionItem(actionWidget);

		ActionDBHelper helper = new ActionDBHelper(v.getContext());

		final WateringAction wateringAction = (WateringAction) helper.getActionByName("water");
		ActionWidget watering = new ActionWidget(v.getContext(), wateringAction);
		watering.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SeedActionInterface actionItem = (SeedActionInterface) wateringAction;
				actionItem.execute(seed);
				parentAdapter.notifyDataSetChanged();
				quickAction.dismiss();

			}
		});
		// watering.setOnActionItemClickListener(new
		// ActionWidget.OnActionItemClickListener() {
		// @Override
		// public void onItemClick(ActionWidget source, BaseActionInterface
		// action) {
		// SeedActionInterface actionItem = (SeedActionInterface) action;
		// actionItem.execute(seed);
		// parentAdapter.notifyDataSetChanged();
		// quickAction.dismiss();
		// }
		// });
		quickAction.addPermanentActionItem(watering);

		final DeleteAction deleteAction = new DeleteAction(v.getContext());
		ActionWidget delete = new ActionWidget(v.getContext(), deleteAction);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage(v.getContext().getResources().getString(R.string.action_delete_seed)).setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SeedActionInterface actionItem = (SeedActionInterface) deleteAction;
								actionItem.execute(seed);
								parentAdapter.notifyDataSetChanged();
								quickAction.dismiss();
								dialog.dismiss();
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.show();

			}
		});
		// delete.setOnActionItemClickListener(new
		// ActionWidget.OnActionItemClickListener() {
		// @Override
		// public void onItemClick(ActionWidget source, BaseActionInterface
		// action) {
		// SeedActionInterface actionItem = (SeedActionInterface) action;
		// actionItem.execute(seed);
		// parentAdapter.notifyDataSetChanged();
		// quickAction.dismiss();
		// }
		// });
		quickAction.addPermanentActionItem(delete);

		final DetailAction detail = new DetailAction(v.getContext());
		ActionWidget detailWidget = new ActionWidget(v.getContext(), new DetailAction(v.getContext()));
		quickAction.addPermanentActionItem(detailWidget);
		detailWidget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SeedActionInterface actionItem = (SeedActionInterface) detail;
				if (DetailAction.class.isInstance(actionItem)) {
					// alert.show();
					final Intent i = new Intent(v.getContext(), TabSeedActivity.class);
					i.putExtra("org.gots.seed.id", ((GrowingSeedInterface) parentView.getTag()).getGrowingSeedId());
					i.putExtra("org.gots.seed.url", ((GrowingSeedInterface) parentView.getTag()).getUrlDescription());

					v.getContext().startActivity(i);
				} else {
					actionItem.execute(seed);
				}
				parentAdapter.notifyDataSetChanged();
				quickAction.dismiss();
			}
		});
		// Set listener for action item clicked
		// detail.setOnActionItemClickListener(new
		// ActionWidget.OnActionItemClickListener() {
		// @Override
		// public void onItemClick(ActionWidget source, BaseActionInterface
		// action) {
		// SeedActionInterface actionItem = (SeedActionInterface) action;
		// if (DetailAction.class.isInstance(actionItem)) {
		// // alert.show();
		// final Intent i = new Intent(v.getContext(), TabSeedActivity.class);
		// i.putExtra("org.gots.seed.id", ((GrowingSeedInterface)
		// v.getTag()).getGrowingSeedId());
		// i.putExtra("org.gots.seed.url", ((GrowingSeedInterface)
		// v.getTag()).getUrlDescription());
		//
		// v.getContext().startActivity(i);
		// } else {
		// actionItem.execute(seed);
		// }
		// parentAdapter.notifyDataSetChanged();
		// quickAction.dismiss();
		// }
		// });

	}

	public void show() {
		quickAction.show(parentView);
	}
}
