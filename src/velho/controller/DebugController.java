package velho.controller;

import velho.view.DebugWindow;

public class DebugController
{
	private DebugWindow view;

	public DebugController(DebugWindow debugView)
	{
		view = debugView;
	}

	public void login()
	{
		System.out.println("Logged in.");
		view.setLogInButton(false);
		view.setLogOutButton(true);
		// TODO Auto-generated method stub

	}

	public void logout()
	{
		System.out.println("Logged out.");
		view.setLogInButton(true);
		view.setLogOutButton(false);
	}

	public void setLogInButton(boolean visibility)
	{
		view.setLogInButton(visibility);
	}

	public void setLogOutButton(boolean visibility)
	{
		view.setLogOutButton(visibility);
	}
}
