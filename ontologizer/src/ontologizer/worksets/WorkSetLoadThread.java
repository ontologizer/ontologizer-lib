package ontologizer.worksets;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import ontologizer.FileCache;
import ontologizer.FileCache.FileCacheUpdateCallback;
import ontologizer.association.AssociationContainer;
import ontologizer.association.AssociationParser;
import ontologizer.association.IAssociationParserProgress;
import ontologizer.go.GOGraph;
import ontologizer.go.IOBOParserProgress;
import ontologizer.go.OBOParser;
import ontologizer.go.TermContainer;
import ontologizer.gui.swt.Ontologizer;
import ontologizer.util.MemoryWarningSystem;

/**
 * Thread which is responsible for loading work set files.
 *
 * @author Sebastian Bauer
 *
 */
public class WorkSetLoadThread extends Thread
{
	private static Logger logger = Logger.getLogger(WorkSetLoadThread.class.getName());

	private static class Message { }
	private static class WorkSetMessage extends Message { public WorkSet workset; }
	private static class ObtainWorkSetMessage extends WorkSetMessage { Runnable callback; public IWorkSetProgress progress;}
	private static class ReleaseWorkSetMessage extends WorkSetMessage { }
	private static class CleanCacheMessage extends Message { }
	private static class CallbackMessage extends Message { Runnable run;}

	/**
	 * The task of downloading obo and association files.
	 *
	 * @author Sebastian Bauer
	 *
	 */
	private static class Task
	{
		public String obo;
		public String assoc;

		private List<Runnable> callbacks = new LinkedList<Runnable>();
		private List<IWorkSetProgress> progresses = new LinkedList<IWorkSetProgress>();

		/**
		 * Add a new callback.
		 *
		 * @param run
		 */
		public void addCallback(Runnable run)
		{
			callbacks.add(run);
		}

		/**
		 * Issue all callbacks.
		 */
		public void issueCallbacks()
		{
			for (Runnable run : callbacks)
				run.run();
		}

		@Override
		public int hashCode()
		{
			return obo.hashCode() + assoc.hashCode();
		}

		@Override
		public boolean equals(Object arg0)
		{
			Task t = (Task)arg0;
			return t.obo.equals(obo) && t.assoc.equals(assoc);
		}

		/**
		 * Checks whether the work set describes this task.
		 *
		 * @param ws
		 * @return
		 */
		public boolean matches(WorkSet ws)
		{
			return ws.getAssociationPath().equals(assoc) && ws.getOboPath().equals(obo);
		}
	}

	static private WorkSetLoadThread wslt;

	static
	{
		wslt = new WorkSetLoadThread();
		wslt.start();
	}

	/**
	 * Obtain the data files for the given WorkSet. Calls run
	 * (in a context of another thread) on completion.
	 *
	 * @param df
	 * @param run
	 */
	static public void obtainDatafiles(WorkSet df, Runnable run)
	{
		obtainDatafiles(df, null, run);
	}

	/**
	 * Obtain the data files for the given WorkSet. Calls run
	 * (in a context of another thread) on completion.
	 *
	 * @param df
	 * @param run
	 */
	static public void obtainDatafiles(WorkSet df, IWorkSetProgress progress, Runnable run)
	{
		ObtainWorkSetMessage owsm = new ObtainWorkSetMessage();
		owsm.workset = df;
		owsm.callback = run;
		owsm.progress = progress;
		wslt.messageQueue.add(owsm);
	}

	/**
	 * Release the data files for the given WorkSet.
	 *
	 * @param df
	 */
	public static void releaseDatafiles(WorkSet df)
	{
		ReleaseWorkSetMessage rwsm = new ReleaseWorkSetMessage();
		rwsm.workset = df;
		wslt.messageQueue.add(rwsm);
	}

	public static void cleanCache()
	{
		CleanCacheMessage cwsm = new CleanCacheMessage();
		wslt.messageQueue.add(cwsm);
	}

	public static AssociationContainer getAssociations(String associationPath)
	{
		String localPath = FileCache.getLocalFileName(associationPath);
		if (localPath == null) return null;
		return wslt.assocMap.get(localPath);
	}

	public static GOGraph getGraph(String oboPath)
	{
		String localPath = FileCache.getLocalFileName(oboPath);
		if (localPath == null) return null;
		return wslt.graphMap.get(localPath);
	}

	/* Private attributes */
	private Map<String,GOGraph> graphMap = Collections.synchronizedMap(new HashMap<String,GOGraph>());
	private Map<String,AssociationContainer> assocMap = Collections.synchronizedMap(new HashMap<String,AssociationContainer>());

	private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

	private List<Task> taskList = new LinkedList<Task>();
	private FileCacheUpdateCallback fileCacheUpdateCallback;

	public WorkSetLoadThread()
	{
		super(Ontologizer.threadGroup,"Work Set Loader Thread");
	}

	@Override
	public void run()
	{
		/* Lower priority */
		setPriority(Thread.MIN_PRIORITY);

		/* Low memory handler */
		MemoryWarningSystem.setPercentageUsageThreshold(0.80);
	    MemoryWarningSystem mws = new MemoryWarningSystem();
	    mws.addListener(new MemoryWarningSystem.Listener() {
	      public void memoryUsageLow(long usedMemory, long maxMemory) {
	    	  logger.warning("Low memory condition! Trying to clean some caches");
	    	  cleanCache();
	      }
	    });

		/* Our callback function */
		fileCacheUpdateCallback = new FileCacheUpdateCallback()
		{
			public void update(final String url)
			{
				if (FileCache.getState(url) == FileCache.FileState.CACHED)
				{
					execAsync(new Runnable()
					{
						public void run()
						{
							/* This is performed inside the work set load thread */
							List <Task> toBeRemoved = new LinkedList<Task>();

							for (Task t : taskList)
							{
								if (url.equals(t.obo) || url.equals(t.assoc))
								{
									if (FileCache.isNonBlocking(t.obo) && FileCache.isNonBlocking(t.assoc))
									{
										loadFiles(FileCache.getLocalFileName(t.obo), FileCache.getLocalFileName(t.assoc), null);
										t.issueCallbacks();
										toBeRemoved.add(t);
									}
								}
							}

							taskList.removeAll(toBeRemoved);
							if (taskList.size() == 0)
								FileCache.removeUpdateCallback(fileCacheUpdateCallback);
						}
					});
				}
			}

			public void exception(Exception exception, String url)
			{
				logger.throwing(this.getClass().getName(), "exception", exception);
			}
		};

		try
		{
			/* Message loop */
			again:
			while (true)
			{
				Message msg =  messageQueue.take();

				if (msg instanceof CallbackMessage)
				{
					((CallbackMessage)msg).run.run();
				} else
				if (msg instanceof CleanCacheMessage)
				{
					graphMap.clear();
					assocMap.clear();
				} else
				if (msg instanceof WorkSetMessage)
				{
					WorkSetMessage wsm = (WorkSetMessage) msg;
					WorkSet ws = wsm.workset;

					if (wsm instanceof ObtainWorkSetMessage)
					{
						/* Check whether stuff has already been loaded. Fire if positive */
						ObtainWorkSetMessage owsm = (ObtainWorkSetMessage) msg;
						if (graphMap.containsKey(ws.getOboPath()) && assocMap.containsKey(ws.getAssociationPath()))
						{
							owsm.callback.run();
							continue again;
						}

						/* Check whether a similar task is pending. Add the callback if positive. */
						for (Task task : taskList)
						{
							if (task.matches(ws))
							{
								task.addCallback(owsm.callback);
								continue again;
							}
						}

						/* Check whether files have already been downloaded */
						String oboName = FileCache.open(ws.getOboPath());
						String assocName = FileCache.open(ws.getAssociationPath());

						if (oboName != null && assocName != null)
						{
							loadFiles(oboName,assocName,owsm.progress);
							owsm.callback.run();
							continue again;
						}

						/* Task wasn't found. The file weren't loaded yet. Create a new task. */
						Task newTask = new Task();
						newTask.assoc = ws.getAssociationPath();
						newTask.obo = ws.getOboPath();
						newTask.addCallback(owsm.callback);
						addTask(newTask);
					} else
					{
						if (wsm instanceof ReleaseWorkSetMessage)
						{
						}
					}
				}
			}
		}
		catch (InterruptedException e)
		{
		}
		catch(Exception e)
		{
			if (!Thread.interrupted())
				e.printStackTrace();
		}
	}

	/**
	 * Add a new task to the task list.
	 *
	 * @param newTask
	 */
	private void addTask(Task newTask)
	{
		if (taskList.size() == 0)
			FileCache.addUpdateCallback(fileCacheUpdateCallback);

		taskList.add(newTask);
	}

	/**
	 * Load the given files. Add them as loaded.
	 *
	 * @param oboName real file names
	 * @param assocName real file names
	 * @param workSetProgress
	 */
	private void loadFiles(String oboName, String assocName, final IWorkSetProgress workSetProgress)
	{
		if (!new File(oboName).exists()) return;
		if (!new File(assocName).exists()) return;

		try
		{
			GOGraph graph;
			if (!graphMap.containsKey(oboName))
			{
				OBOParser oboParser = new OBOParser(oboName);
				workSetProgress.message("Parsing OBO file");
				oboParser.doParse(new IOBOParserProgress()
				{

					public void init(int max)
					{
						workSetProgress.initGauge(max);
					}

					public void update(int current, int terms)
					{
						workSetProgress.message("Parsing OBO file ("+terms+")");
						workSetProgress.updateGauge(current);
					}
				});
				TermContainer goTerms = new TermContainer(oboParser.getTermMap(), oboParser.getFormatVersion(), oboParser.getDate());
				workSetProgress.message("Building GO graph");
				graph = new GOGraph(goTerms);
				graphMap.put(oboName,graph);
			} else
			{
				graph = graphMap.get(oboName);
			}

			if (!assocMap.containsKey(assocName))
			{
				workSetProgress.message("Parsing association file");
				workSetProgress.updateGauge(0);
				AssociationParser ap = new AssociationParser(assocName,graph.getGoTermContainer(),null,new IAssociationParserProgress()
				{
					public void init(int max)
					{
						workSetProgress.initGauge(max);
					}

					public void update(int current)
					{
						workSetProgress.updateGauge(current);
					}
				});

				AssociationContainer ac = new AssociationContainer(ap.getAssociations(), ap.getSynonym2gene(), ap.getDbObject2gene());
				assocMap.put(assocName, ac);
				workSetProgress.message("");
				workSetProgress.initGauge(0);
			}
		} catch (Exception e)
		{
			logger.throwing(this.getClass().getName(), "loadFiles", e);
		}
	}


	/**
	 * Execute a runnable in the context of this thread.
	 *
	 * @param run
	 */
	private void execAsync(Runnable run)
	{
		CallbackMessage cmsg = new CallbackMessage();
		cmsg.run = run;
		messageQueue.add(cmsg);
	}

}
