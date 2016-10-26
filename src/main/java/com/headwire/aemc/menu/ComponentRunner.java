package com.headwire.aemc.menu;

import java.util.Map;

import com.headwire.aemc.command.CreateCommand;
import com.headwire.aemc.command.DeleteCommand;
import com.headwire.aemc.command.Menu;

/**
 * Component creator
 *
 */
public class ComponentRunner implements BasisRunner {

	// Invoker
	private Menu menu = new Menu();
	public Map<String, String> params;

	/**
	 * Constructor
	 * 
	 * @param params
	 *            - params
	 */
	public ComponentRunner(Map<String, String> params) {
		this.params = params;

		// Creates Invoker object, command object and configure them
		menu.setCommand("Create", new CreateCommand(params));
		menu.setCommand("Delete", new DeleteCommand(params));
	}

	/**
	 * Run commands
	 */
	@Override
	public void run() {
		// Invoker invokes command
		menu.runCommand("Create");
		menu.runCommand("Delete");
	}
}
