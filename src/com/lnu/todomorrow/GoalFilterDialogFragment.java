package com.lnu.todomorrow;

import java.util.List;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.utils.Goal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GoalFilterDialogFragment extends DialogFragment {
	private static final String TAG = GoalFilterDialogFragment.class.getSimpleName();
	private ArrayAdapter<Goal> dataAdapter;

	public interface GoalFilterDialogListener {
		public void onGoalFilterDialogSelection(Goal selectedGoal);
	}

	private GoalFilterDialogListener dialogListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {

			dialogListener = (GoalFilterDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement GoalFilterDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		GoalDAO dataGoal = new GoalDAO(getActivity());
		dataGoal.open();
		List<Goal> goals = dataGoal.getAllGoals();

		dataAdapter = new ArrayAdapter<Goal>(getActivity(), android.R.layout.select_dialog_item,
				goals) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv;
				if (convertView == null) {
					tv = new TextView(getActivity());
				} else {
					tv = (TextView) convertView;
				}
				tv.setText(getItem(position).getName());
				return tv;
			}
		};

		builder.setTitle(R.string.dialog_goal_selection_title);
		builder.setAdapter(dataAdapter, new SelectionClickListener());
		builder.setNegativeButton(R.string.dialog_goal_selection_cancel, new CancelClickListener());

		Log.d(TAG, "creating dialog");

		return builder.create();
	}

	private class SelectionClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Goal goal = dataAdapter.getItem(which);
			Log.d(TAG, "Selected goal: " + goal.getName());
			dialogListener.onGoalFilterDialogSelection(goal);
		}

	}

	private class CancelClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d(TAG, "Clicked Cancel");
			GoalFilterDialogFragment.this.getDialog().cancel();
		}

	}
}
