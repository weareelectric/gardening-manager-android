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
package org.gots.seed.adapter;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.view.MonthWidget;

import android.view.View;
import android.view.ViewGroup;

public class PlanningSowAdapter extends PlanningAdapter {
	BaseSeedInterface mSeed;

	public PlanningSowAdapter(BaseSeedInterface seed) {
		mSeed = seed;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MonthWidget monthWidget = new MonthWidget(parent.getContext());
		monthWidget.setMonthText(getItem(position));
//		monthWidget.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.selector_planning));

		if (mSeed != null) {

			if (position >= mSeed.getDateSowingMin() && position <= mSeed.getDateSowingMax())
				monthWidget.setSowingPeriode(true);

			
		}
		monthWidget.invalidate();
		return monthWidget;
	}
	
	

}
