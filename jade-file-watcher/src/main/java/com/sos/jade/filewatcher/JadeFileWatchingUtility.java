/**
 *
 */
package com.sos.jade.filewatcher;

/**
 * @author KB
 *
 * further readings:
 *  http://markusjais.com/file-system-events-with-java-7/
 *
 */

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class JadeFileWatchingUtility  implements Runnable {

	@SuppressWarnings("unused")
	private final String				conClassName		= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String			conSVNVersion		= "$Id$";
	private static Logger				logger				= Logger.getLogger(JadeFileWatchingUtility.class);

	private final WatchService			objWatchService;
	private final Map<WatchKey, Path>	mapWatchKeys;
	private boolean						recursive			= false;
	private boolean						flgTraceIsActive	= false;
	private Path strFolderName2Watch4 = null;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(final WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public JadeFileWatchingUtility(final Path pstrFolderName2Watch, final boolean pflgRecursive) throws IOException {

//		BasicConfigurator.configure();
//		logger.setLevel(Level.DEBUG);

		objWatchService = FileSystems.getDefault().newWatchService();
		mapWatchKeys = new HashMap<>();
		recursive = pflgRecursive;
		strFolderName2Watch4 = pstrFolderName2Watch;

		if (recursive) {
			logger.debug(String.format("Start recursive watching for %s ...\n", pstrFolderName2Watch.getFileName()));
			registerAll(pstrFolderName2Watch);
//			logger.info("Done.");
		}
		else {
			logger.debug(String.format("Start non-recursive watching for %s ...\n", pstrFolderName2Watch.getFileName()));
			register(pstrFolderName2Watch);
		}

		// enable trace after initial registration
		flgTraceIsActive = true;
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	public void processEvents() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = objWatchService.take();
			}
			catch (InterruptedException x) {
				logger.debug(String.format("FileWatcher interrupted for Folder '%1$s'", strFolderName2Watch4.getFileName()));
				return;
			}

			Path dir = mapWatchKeys.get(key);
			if (dir == null) {
				logger.error("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					logger.debug(String.format("Overflow occurred processing Folder %1$s", strFolderName2Watch4));
					continue;
				}

				// TODO check if the action has to be processed

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				logger.debug(String.format("%s: %s\n", event.kind().name(), child));

				// TODO check for regexp
				// TODO excecute required action

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && kind == ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					}
					catch (IOException x) {
						throw new JobSchedulerException(x);
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				mapWatchKeys.remove(key);

				// all directories are inaccessible
				if (mapWatchKeys.isEmpty()) {
					break;
				}
			}
		}
	}

	/**
	 *
	*
	* \brief registerAll
	*
	* \details
	*
	* Register the given directory, and all its sub-directories, with the
	* WatchService.
	* \return void
	*
	 */

	private void registerAll(final Path pstrStartFolderName) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(pstrStartFolderName, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(final Path dir) throws IOException {
		WatchKey key = dir.register(objWatchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (flgTraceIsActive) {
			Path prev = mapWatchKeys.get(key);
			if (prev == null) {
				logger.debug(String.format("register: %s\n", dir));
			}
			else {
				if (!dir.equals(prev)) {
					logger.debug(String.format("update: %s -> %s\n", prev, dir));
				}
			}
		}
		mapWatchKeys.put(key, dir);
	}

	static void usage() {
		System.err.println("usage: java JadeFileWatchingUtility [-r] dir [dir ...]");
		System.exit(-1);
	}

	public static void main(final String[] args) throws IOException {

		// parse arguments
		if (args.length == 0 ) {
			usage();
		}
		boolean flgRecurseSubFolders = false;
		int intNoOfFolders2WatchFor = args.length;
		int dirArg = 0;
		if (args[0].equals("-r")) {
			intNoOfFolders2WatchFor--;
			if (args.length < 2) {
				usage();
			}
			flgRecurseSubFolders = true;
			dirArg++;
		}

		SOSThreadPoolExecutor mtpe = new SOSThreadPoolExecutor(intNoOfFolders2WatchFor);
		int cpus = Runtime.getRuntime().availableProcessors();
		System.out.println("max avl cpus = " + cpus) ;

		@SuppressWarnings("unchecked")
		Future<Runnable>[] objRunningThreads = new Future[intNoOfFolders2WatchFor];
		int intThread = 0;
		for (int i = dirArg; i < args.length; i++) {
			// register directory and process its events
			Path dir = Paths.get(args[i]);
			Future<Runnable> objF = mtpe.runTask(new JadeFileWatchingUtility(dir, flgRecurseSubFolders));
			objRunningThreads [intThread++] = objF;
			// TODO for time dependent threads use a shared variable or interrupt
		}

		try {  // runtime in general
			Thread.sleep (20000);
			for (int i = 0; i < intNoOfFolders2WatchFor; i++) {
				Future <Runnable> objF = objRunningThreads[i];
				objF.cancel(true);
			}
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO stopping may be: running for xxx hh:min:ss
		//              a command received by tcp/ip
		//              a file created in a folder
		//              ....
		mtpe.shutDown();
		logger.info("Terminate");
		System.exit (0);
	}

	@Override
	public void run() {
		this.processEvents();
	}
}
