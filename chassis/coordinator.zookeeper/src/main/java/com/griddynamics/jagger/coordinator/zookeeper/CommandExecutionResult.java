/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.coordinator.zookeeper;

import java.io.Serializable;

public class CommandExecutionResult implements Serializable {
	private CommandExecutionStatus status;
	private Serializable result;
    private Throwable exception;

	public static CommandExecutionResult success(Serializable result) {
		CommandExecutionResult commandExecutionResult = new CommandExecutionResult();
        commandExecutionResult.setResult(result);
		commandExecutionResult.setStatus(CommandExecutionStatus.SUCCEEDED);
		return commandExecutionResult;
	}

	public static CommandExecutionResult fail(Throwable throwable) {
		CommandExecutionResult commandExecutionResult = new CommandExecutionResult();
		commandExecutionResult.setStatus(CommandExecutionStatus.FAILED);
		commandExecutionResult.setException(throwable);
		return commandExecutionResult;
	}

	public CommandExecutionStatus getStatus() {
		return status;
	}

	public void setStatus(CommandExecutionStatus status) {
		this.status = status;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }

    private static final long serialVersionUID = -5933277208904927307L;

}

