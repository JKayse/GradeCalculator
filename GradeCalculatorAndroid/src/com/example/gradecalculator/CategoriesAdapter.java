package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CategoriesAdapter extends BaseExpandableListAdapter{

	LayoutInflater inflater;
	Context context;
	Category[] categories;

	CategoriesAdapter(Context context, Category[] data){
		super();
		this.context = context;
		categories = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public Grade getChild(int groupPosition, int childPosition) {
        return categories[groupPosition].getGrade(childPosition);
    }
	
	public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final Grade currentGrade = getChild(groupPosition, childPosition);
         
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grade_item, null);
        }
         
        TextView gradeName = (TextView) convertView.findViewById(R.id.gradeName);
        TextView gradePercentage = (TextView) convertView.findViewById(R.id.percentage);
        gradeName.setText(currentGrade.getGradeName());
        gradePercentage.setText(currentGrade.getPercentage()+"%");
        return convertView;
	}
	
	public View getGroupView(int groupPosition, boolean isExpanded,
	            View convertView, ViewGroup parent) {
			Category temp = getGroup(groupPosition);
	        if (convertView == null) {
	            LayoutInflater secondInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = secondInflater.inflate(R.layout.category_item,null);
	        }
	        TextView categoryName = (TextView) convertView.findViewById(R.id.category);
	        TextView percentage = (TextView) convertView.findViewById(R.id.percentage);
	        categoryName.setText(temp.getCategoryName());
	        percentage.setText(temp.getPercentage()+"%");

	        return convertView;
	    }

	@Override
	public int getGroupCount() {
		return categories.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return categories[groupPosition].getGrades().length;
	}

	@Override
	public Category getGroup(int groupPosition) {
		return categories[groupPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
