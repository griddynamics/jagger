package com.griddynamics.jagger.kernel;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.CommandExecutor;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.storage.KeyValueStorage;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 5/22/14
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */


/**
 * This class is required for setting sessionId into class KeyValueStorage for every kernel.
 * We implemented "execute(...)" for this purpose. It is called "doExecute()" which is should be implement for every received command.
 *
 * @param <C> is a some command
 * @param <R> is a result of the execution
 */

abstract public class WorkloadWorkerCommandExecutor<C extends Command<R>, R extends Serializable> implements CommandExecutor <C,R> {

    @Override
    public R execute(C command, NodeContext nodeContext) {
        KeyValueStorage keyValueStorage = nodeContext.getService(KeyValueStorage.class);
        if (keyValueStorage != null)
            keyValueStorage.setSessionId(command.getSessionId());
        return doExecute(command, nodeContext);
    }

    abstract public R doExecute(C command, NodeContext nodeContext);
}
