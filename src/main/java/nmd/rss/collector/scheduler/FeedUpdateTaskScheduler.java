package nmd.rss.collector.scheduler;

import java.util.List;

import static nmd.rss.collector.scheduler.FeedUpdateTaskSchedulerContext.START_CONTEXT;
import static nmd.rss.collector.util.Assert.assertNotNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 30.04.13
 */
public class FeedUpdateTaskScheduler {

    private final FeedUpdateTaskSchedulerContextRepository contextRepository;
    private final FeedUpdateTaskRepository taskRepository;

    public FeedUpdateTaskScheduler(final FeedUpdateTaskSchedulerContextRepository contextRepository, final FeedUpdateTaskRepository taskRepository) {
        assertNotNull(contextRepository);
        this.contextRepository = contextRepository;

        assertNotNull(taskRepository);
        this.taskRepository = taskRepository;
    }

    public FeedUpdateTask getCurrentTask() throws FeedUpdateTaskSchedulerException {

        try {
            FeedUpdateTaskSchedulerContext context = this.contextRepository.load();
            context = context == null ? START_CONTEXT : context;

            List<FeedUpdateTask> tasks = this.taskRepository.loadAllTasks();

            if (tasks.isEmpty()) {
                return null;
            }

            int taskIndex = context.lastTaskIndex + 1;
            taskIndex = taskIndex > tasks.size() - 1 ? 0 : taskIndex;

            FeedUpdateTaskSchedulerContext newContext = new FeedUpdateTaskSchedulerContext(taskIndex);
            this.contextRepository.store(newContext);

            return tasks.get(taskIndex);
        } catch (FeedUpdateTaskSchedulerContextRepositoryException | FeedUpdateTaskRepositoryException exception) {
            throw new FeedUpdateTaskSchedulerException(exception);
        }
    }

}