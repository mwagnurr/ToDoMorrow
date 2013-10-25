package com.lnu.todomorrow.utils;

public class Task {

	private long id;
	private String name;
	private Goal goal;
	private String deadline;
	private boolean finished;
	private int value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", goal=" + goal + ", deadline=" + deadline
				+ ", finished=" + finished + ", value=" + value + "]";
	}
	
}
