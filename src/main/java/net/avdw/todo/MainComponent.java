package net.avdw.todo;

import dagger.Component;
import net.avdw.todo.core.AddCli;
import net.avdw.todo.core.ArchiveCli;
import net.avdw.todo.core.BackupCli;
import net.avdw.todo.core.CoreModule;
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
import net.avdw.todo.extension.ExtensionModule;
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
import net.avdw.update.UpdateModule;
import net.avdw.update.adapter.in.UpdateCliAdapter;

import javax.inject.Singleton;

@Singleton
@Component(modules = {MainModule.class, CoreModule.class, ExtensionModule.class, UpdateModule.class})
interface MainComponent {
    MainCli mainCli();
    AddCli addCli();
    ListCli listCli();
    PriorityCli priorityCli();
    ArchiveCli archiveCli();
    BackupCli backupCli();
    ChangelogCli changelogCli();
    CommentCli commentCli();
    DoneCli doneCli();
    EditCli editCli();
    InitCli initCli();
    DependencyCli linkCli();
    ParkCli parkCli();
    RemoveCli removeCli();
    SortCli sortCli();
    CleanMixin cleanMixin();
    RepositoryMixin repositoryMixin();
    GroupByMixin groupByMixin();
    TimingMixin timingMixin();
    ChangeMixin changeMixin();
    OrderByMixin orderByMixin();
    StateMixin stateMixin();
    TemplatedResource templatedResource();
    TodoListView todoListView();
    MoscowCli moscowCli();
    PlanCli planCli();
    ReplaceCli replaceCli();
    SizeCli sizeCli();
    StartCli startCli();
    StatusCli statusCli();
    StatsCli statsCli();
    BrowseCli browseCli();

    UpdateCliAdapter updateCliAdapter();
}
