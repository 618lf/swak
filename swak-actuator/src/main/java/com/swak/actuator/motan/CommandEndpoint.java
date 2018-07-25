package com.swak.actuator.motan;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.motan.manager.CommandService;
import com.swak.utils.StringUtils;
import com.weibo.api.motan.registry.support.command.RpcCommand.ClientCommand;

/**
 * {@link Endpoint} to expose details of motan Command.
 * 
 * @author lifeng
 */
@Endpoint(id = "motanCommand")
public class CommandEndpoint {

	private final CommandService commandService;

	public CommandEndpoint(CommandService commandService) {
		this.commandService = commandService;
	}

	/**
	 * 命令
	 * 
	 * @return
	 */
	@Operation
	public List<JSONObject> commands() {
		return commandService.getAllCommands();
	}

	/**
	 * 命令
	 * 
	 * @return
	 */
	@Operation
	public String commands(@Selector String group) {
		if (StringUtils.isEmpty(group)) {
			return StringUtils.EMPTY;
		}
		return commandService.getCommands(group);
	}

	/**
	 * 向指定group添加指令
	 * 
	 * @param group
	 * @param clientCommand
	 * @return
	 */
	@Operation
	public Boolean addCommand(@Selector String group, ClientCommand clientCommand) {
		if (StringUtils.isEmpty(group) || clientCommand == null) {
			return Boolean.FALSE;
		}
		return commandService.addCommand(group, clientCommand);
	}

	/**
	 * 向指定group修改指令
	 * 
	 * @param group
	 * @param clientCommand
	 * @return
	 */
	@Operation
	public Boolean updateCommand(@Selector String group, ClientCommand clientCommand) {
		if (StringUtils.isEmpty(group) || clientCommand == null) {
			return Boolean.FALSE;
		}
		return commandService.updateCommand(group, clientCommand);
	}

	/**
	 * 向指定group添加指令
	 * 
	 * @param group
	 * @param clientCommand
	 * @return
	 */
	@Operation
	public Boolean deleteCommand(@Selector String group, int index) {
		if (StringUtils.isEmpty(group)) {
			return Boolean.FALSE;
		}
		return commandService.deleteCommand(group, index);
	}

	/**
	 * 向指定group添加指令
	 * 
	 * @param group
	 * @param clientCommand
	 * @return
	 */
	@Operation
	public List<JSONObject> previewCommand(@Selector String group, @Selector String previewIP, ClientCommand clientCommand) {
		if (StringUtils.isEmpty(group) || clientCommand == null) {
			return Lists.newArrayList();
		}
		return commandService.previewCommand(group, clientCommand, previewIP);
	}
}