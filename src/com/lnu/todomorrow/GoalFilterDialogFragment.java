package com.lnu.todomorrow;

import java.util.ArrayList;
import java.util.List;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.utils.Goal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.util.Log;

public class GoalFilterDialogFragment extends DialogFragment {
	private static final String TAG = GoalFilterDialogFragment.class.getSimpleName();

	private List<Goal> goals;
	private List<Goal> goalsFilterList;
	private boolean[] goalChecked;

	/**
	 * Callback interface which has to be implemented by the activity using this fragment
	 */
	public interface GoalFilterDialogListener {
		public void onGoalFilterDialogSubmit(List<Goal> filteredGoals);
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
		goals = dataGoal.getAllGoals();
		goalsFilterList = new ArrayList<Goal>();

		builder.setTitle(R.string.dialog_goal_selection_title);

		String[] goalNames = new String[goals.size()];
		goalChecked = new boolean[goals.size()];

		for (int i = 0; i < goals.size(); i++) {
			goalNames[i] = goals.get(i).getName();
			goalChecked[i] = false;
		}
		builder.setMultiChoiceItems(goalNames, goalChecked, new MultiChoiceSlectionListener());
		builder.setPositiveButton(android.R.string.ok, new OkClickListener());
		builder.setNegativeButton(android.R.string.cancel, new CancelClickListener());

		Log.d(TAG, "creating dialog");

		return builder.create();
	}

	private class MultiChoiceSlectionListener implements OnMultiChoiceClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			Goal goal = goals.get(which);
			if (isChecked) {
				Log.d(TAG, "adding goal " + goal.getName());
				goalsFilterList.add(goal);

			} else {
				Log.d(TAG, "removing goal " + goal.getName());
				if (!goalsFilterList.remove(goal))
					Log.e(TAG, "goal not in list");
			}

		}

	}

	private class OkClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d(TAG, "Clicked Ok - filterList size: " + goalsFilterList.size());
			dialogListener.onGoalFilterDialogSubmit(goalsFilterList);
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
