/*******************************************************************************
 * Copyright (c) 2009, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.test.perf;

import java.util.concurrent.Callable;

/**
 * Base class for execution time test scenarios.
 */
public abstract class TimedScenario implements IPerfScenario {

	private static final int RUNS = 10;

	private final String description;

	protected TimedScenario(final String description) {
		this.description = description;
	}

	public void run(final IPerfOutput output) throws Exception {
		final long time = getMinimumTime(getInstrumentedCallable());
		final Callable<Void> refRunnable = getReferenceCallable();
		final long reftime;
		if (refRunnable == null) {
			reftime = IPerfOutput.NO_REFERENCE;
		} else {
			reftime = getMinimumTime(refRunnable);
		}
		output.writeTimeResult(description, time, reftime);
	}

	/**
	 * Runs the given subject several times and returns the minimum execution
	 * time.
	 * 
	 * @param subject
	 * @return minimum execution time in nano seconds
	 * @throws Exception
	 */
	private long getMinimumTime(final Callable<Void> subject) throws Exception {
		long min = Long.MAX_VALUE;
		for (int i = 0; i < RUNS; i++) {
			final long t = getTime(subject);
			min = Math.min(min, t);
		}
		return min;
	}

	private long getTime(final Callable<Void> subject) throws Exception {
		long start = System.nanoTime();
		subject.call();
		return System.nanoTime() - start;
	}

	protected abstract Callable<Void> getInstrumentedCallable()
			throws Exception;

	protected Callable<Void> getReferenceCallable() throws Exception {
		return null;
	}

}
