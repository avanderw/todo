package net.avdw.todo;

import net.avdw.todo.core.AddCli;
import net.avdw.todo.core.ArchiveCli;
import net.avdw.todo.core.BackupCli;
import net.avdw.todo.core.DoneCli;
import net.avdw.todo.core.InitCli;
import net.avdw.todo.core.ListCli;
import net.avdw.todo.core.OrderByMixin;
import net.avdw.todo.core.ParkCli;
import net.avdw.todo.core.PriorityCli;
import net.avdw.todo.core.RemoveCli;
import net.avdw.todo.core.SortCli;
import net.avdw.todo.core.StatusCli;
import net.avdw.todo.core.groupby.GroupByMixin;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.core.mixin.RepositoryMixin;
import net.avdw.todo.core.view.TodoListView;
import net.avdw.todo.extension.browse.BrowseCli;
import net.avdw.todo.extension.change.ChangeMixin;
import net.avdw.todo.extension.changelog.ChangelogCli;
import net.avdw.todo.extension.comment.CommentCli;
import net.avdw.todo.extension.dependency.DependencyCli;
import net.avdw.todo.extension.edit.EditCli;
import net.avdw.todo.extension.moscow.MoscowCli;
import net.avdw.todo.extension.plan.PlanCli;
import net.avdw.todo.extension.replace.ReplaceCli;
import net.avdw.todo.extension.size.SizeCli;
import net.avdw.todo.extension.start.StartCli;
import net.avdw.todo.extension.state.StateMixin;
import net.avdw.todo.extension.stats.StatsCli;
import net.avdw.todo.extension.timing.TimingMixin;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public class DaggerFactory implements CommandLine.IFactory {
    private final CommandLine.IFactory fallbackFactory = CommandLine.defaultFactory();
    private final MainComponent mainComponent;
    private final Map<String, Object> cache = new HashMap<>();

    public DaggerFactory(final MainComponent mainComponent) {
        this.mainComponent = mainComponent;
    }

    @Override
    public <K> K create(final Class<K> aClass) throws Exception {
        if (cache.isEmpty()) {
            createCache();
        }
        if (cache.containsKey(aClass.getCanonicalName())) {
            return (K) cache.get(aClass.getCanonicalName());
        } else {
            Logger.debug("Falling back to picocli injector for {}", aClass);
            return fallbackFactory.create(aClass);
        }
    }

    private void createCache() {
        cache.put(MainCli.class.getCanonicalName(), mainComponent.mainCli());
        cache.put(AddCli.class.getCanonicalName(), mainComponent.addCli());
        cache.put(ArchiveCli.class.getCanonicalName(), mainComponent.archiveCli());
        cache.put(BackupCli.class.getCanonicalName(), mainComponent.backupCli());
        cache.put(BrowseCli.class.getCanonicalName(), mainComponent.browseCli());
        cache.put(ChangelogCli.class.getCanonicalName(), mainComponent.changelogCli());
        cache.put(CommentCli.class.getCanonicalName(), mainComponent.commentCli());
        cache.put(DoneCli.class.getCanonicalName(), mainComponent.doneCli());
        cache.put(EditCli.class.getCanonicalName(), mainComponent.editCli());
        cache.put(InitCli.class.getCanonicalName(), mainComponent.initCli());
        cache.put(DependencyCli.class.getCanonicalName(), mainComponent.linkCli());
        cache.put(ListCli.class.getCanonicalName(), mainComponent.listCli());
        cache.put(MoscowCli.class.getCanonicalName(), mainComponent.moscowCli());
        cache.put(ParkCli.class.getCanonicalName(), mainComponent.parkCli());
        cache.put(PlanCli.class.getCanonicalName(), mainComponent.planCli());
        cache.put(PriorityCli.class.getCanonicalName(), mainComponent.priorityCli());
        cache.put(RemoveCli.class.getCanonicalName(), mainComponent.removeCli());
        cache.put(ReplaceCli.class.getCanonicalName(), mainComponent.replaceCli());
        cache.put(SizeCli.class.getCanonicalName(), mainComponent.sizeCli());
        cache.put(SortCli.class.getCanonicalName(), mainComponent.sortCli());
        cache.put(StartCli.class.getCanonicalName(), mainComponent.startCli());
        cache.put(StatusCli.class.getCanonicalName(), mainComponent.statusCli());
        cache.put(StatsCli.class.getCanonicalName(), mainComponent.statsCli());

        cache.put(CleanMixin.class.getCanonicalName(), mainComponent.cleanMixin());
        cache.put(RepositoryMixin.class.getCanonicalName(), mainComponent.repositoryMixin());
        cache.put(GroupByMixin.class.getCanonicalName(), mainComponent.groupByMixin());
        cache.put(TimingMixin.class.getCanonicalName(), mainComponent.timingMixin());
        cache.put(ChangeMixin.class.getCanonicalName(), mainComponent.changeMixin());
        cache.put(OrderByMixin.class.getCanonicalName(), mainComponent.orderByMixin());
        cache.put(StateMixin.class.getCanonicalName(), mainComponent.stateMixin());

        cache.put(TemplatedResource.class.getCanonicalName(), mainComponent.templatedResource());
        cache.put(TodoListView.class.getCanonicalName(), mainComponent.todoListView());
    }
}
