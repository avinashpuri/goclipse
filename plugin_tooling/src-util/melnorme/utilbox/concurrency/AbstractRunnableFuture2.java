/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.concurrency;

public abstract class AbstractRunnableFuture2<RET> extends AbstractFuture2<RET> 
	implements IRunnableFuture2<RET>
{
	
	public AbstractRunnableFuture2() {
		super();
	}
	
	/**
	 * CancellableTask is use for:
	 * - not run task if task already cancelled.
	 * - if a thread is currently running the internalTaskRun, interrupt the thread if future is cancelled.
	 */
	private final CancellableTask cancellableTask = new CancellableTask() {
		@Override
		protected void doRun() {
			AbstractRunnableFuture2.this.internalTaskRun();
		}
	};
	
	@Override
	public boolean canExecute() {
		return cancellableTask.canExecute();
	}
	
	@Override
	public void run() {
		runFuture();
	}
	
	protected void runFuture() {
		cancellableTask.run();
	}
	
	protected void internalTaskRun() {
		completableResult.setResultFromCallable(this::internalInvoke);
	}
	
	protected abstract RET internalInvoke();
	
	
	public void completeWithResult(RET result) {
		cancellableTask.markExecuted();
		completableResult.setResult(result);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected void handleCancellation() {
		cancellableTask.tryCancel();
	}
	
}