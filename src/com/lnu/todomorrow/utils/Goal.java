package com.lnu.todomorrow.utils;

import java.io.Serializable;
import java.util.Calendar;

public class Goal implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private int score;
	private Calendar deadline;

	public Goal(int id, String name, int score, Calendar deadline) {
		this.id = id;
		this.name = name;
		this.score = score;
		this.deadline = deadline;
	}

	public Goal(int id, String name, int score) {
		this(id, name, score, null);
	}

	public Goal(int id, String name, Calendar deadline) {
		this(id, name, 0, deadline);
	}

	public Goal(int id, String name) {
		this(id, name, 0, null);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Calendar getDeadline() {
		return deadline;
	}

	public void setDeadline(Calendar deadline) {
		this.deadline = deadline;
	}

	public void addScore(int score) {
		this.score += score;
	}

	@Override
	public String toString() {
		return "Goal [id=" + id + ", name=" + name + ", score=" + score + ", deadline="
				+ TimeUtil.getFormattedDate(deadline) + "]";
	}

}
