package com.lnu.todomorrow.dao;

public class DAOException extends Exception {
	private static final long serialVersionUID = 1L;

	public DAOException(String message) {
		super(message);
	}

	public DAOException(Throwable throwable) {
		super(throwable);
	}

	public DAOException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
