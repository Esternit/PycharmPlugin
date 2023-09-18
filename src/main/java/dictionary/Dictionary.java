package dictionary;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.spellchecker.settings.SpellCheckerSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Dictionary {

    private Project project;
    private SpellCheckerSettings spellCheckerSettings;

    public Dictionary(Project project) {
        this.project = project;
        this.spellCheckerSettings = SpellCheckerSettings.getInstance(project);
    }

    public Collection<String> register(Collection<VirtualFile> files)
    {
        List<String> paths = spellCheckerSettings.getCustomDictionariesPaths();

        Collection<String> filePaths = files.stream()
            .map(VirtualFile::getPath)
            .filter(path -> !paths.contains(path))
            .collect(Collectors.toList());


        paths.addAll(filePaths);

        return filePaths;
    }

    public Collection<String> remove(Collection<VirtualFile> files)
    {
        List<String> paths = spellCheckerSettings.getCustomDictionariesPaths();

        Collection<String> filePaths = files.stream()
            .map(VirtualFile::getPath)
            .filter(paths::contains)
            .collect(Collectors.toList());

        paths.removeAll(filePaths);

        return filePaths;
    }

    public void moveAndNotify(VirtualFile oldFile, VirtualFile newFile)
    {
        Collection<String> removed = remove(Collections.singletonList(oldFile));
        Collection<String> added = register(Collections.singletonList(newFile));

        if(!removed.isEmpty() && !added.isEmpty()) {
            Notifications.Bus.notify(
                new Notification(
                    "Dictionary register",
                    "Moved dictionaries",
                    "Moved ["+oldFile.getPath()+"] to ["+newFile.getPath()+"]",
                    NotificationType.INFORMATION
                ),
                project
            );
        }

    }

    public void removeAndNotify(VirtualFile file)
    {
        Collection<VirtualFile> files = new ArrayList<>();
        files.add(file);
        Collection<String> filePaths = remove(files);

        if (!filePaths.isEmpty()) {
            Notifications.Bus.notify(
                new Notification(
                    "Dictionary register",
                    "Removed dictionaries",
                    "Removed the following dictionaries ["+ String.join("", filePaths)+"]",
                    NotificationType.INFORMATION
                ),
                project
            );
        }
    }

    public void registerAndNotify(VirtualFile file)
    {
        Collection<VirtualFile> files = new ArrayList<>();
        files.add(file);
        registerAndNotify(files);
    }

    public void registerAndNotify(Collection<VirtualFile> files)
    {
        Collection<String> filePaths = register(files);

        if (!filePaths.isEmpty()) {
            Notifications.Bus.notify(
                new Notification(
                    "Dictionary register",
                    "Found dictionaries",
                    "Registered the following dictionaries ["+ String.join("", filePaths)+"]",
                    NotificationType.INFORMATION
                ),
                project
            );
        }
    }
}
