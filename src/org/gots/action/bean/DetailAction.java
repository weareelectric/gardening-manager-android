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
package org.gots.action.bean;

import java.util.Date;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;

public class DetailAction extends AbstractActionSeed implements PermanentActionInterface, SeedActionInterface, GardeningActionInterface {
	Context mContext; 

	public DetailAction(Context context) {
		setName("detail");
		mContext = context;
	}

	@Override
	public int execute(GrowingSeedInterface seed) {
		super.execute(seed);
		 
//		GrowingSeedDBHelper helper = new GrowingSeedDBHelper(mContext);
//		helper.deleteGrowingSeed(seed);
		return 1;
	}

	public void setDateActionDone(Date dateActionDone) {
		super.setDateActionDone(dateActionDone);
	}

	public Date getDateActionDone() {
		return super.getDateActionDone();
	}

	public void setDuration(int duration) {
		super.setDuration(duration);
	}

	public int getDuration() {
		return super.getDuration();
	}

	public void setDescription(String description) {
		super.setDescription(description);
	}

	public String getDescription() {
		return super.getDescription();
	}

	public void setName(String name) {
		super.setName(name);
	}

	public String getName() {
		return super.getName();
	}

	@Override
	public int execute(BaseAllotmentInterface allotment, GrowingSeedInterface seed) {
		
	
		return 0;
	}

	@Override
	public void setId(int id) {
		super.setId(id);
	}

	@Override
	public int getId() {
		return super.getId();
	}

	public void setData(Object data) {
	}

	public Object getData() {
		return null;
	}

}
