package dictionary;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DictionaryFileListener implements VirtualFileListener {
    private Dictionary dictionary;
    private Project project;
    private Editor editor;
    private LogicalPosition pos;

    public DictionaryFileListener(Dictionary dictionary, Project project) {
        this.dictionary = dictionary;
        this.project=project;
        this.editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event){
        if (event.getFileName().equals("project.dic")){
            try {
                FileReader fread=new FileReader("D:\\Dima\\Python\\codechanger\\project.dic");
                Scanner scan = new Scanner(fread);
                int num= scan.nextInt();
                editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                FileWriter writer = new FileWriter("D:\\Dima\\Python\\codechanger\\notes3.txt", false);


                writer.write("Ok");
                pos = new  LogicalPosition(num,1);
                writer.flush();
                editor.getScrollingModel().scrollTo(pos, ScrollType.CENTER);
                writer.close();
                fread.close();
            } catch (IOException e) {
                System.out.println("Ошибка");
            }
        }

    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        if(event.getPropertyName().equals(VirtualFile.PROP_NAME)) {


            try {
                FileWriter writer = new FileWriter("D:\\Dima\\Python\\codechanger\\notes3.txt", false);
                writer.write("OK");
                writer.write(VirtualFile.PROP_NAME);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            if(event.getFileName().equals("project.dic")) {
                dictionary.registerAndNotify(event.getFile());
            }

            if(event.getOldValue().equals("project.dic")) {
                String path = event.getFile().getPath().substring(0, event.getFile().getPath().length() - ((String)event.getNewValue()).length())+event.getOldValue();
                VirtualFile file = new FakeVirtualFile(path);
                dictionary.removeAndNotify(file);
            }
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        if(event.getFileName().equals("project.dic")) {
            dictionary.registerAndNotify(event.getFile());
        }
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        if(event.getFileName().equals("project.dic")) {
            dictionary.removeAndNotify(event.getFile());
        }
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        dictionary.moveAndNotify(
            new FakeVirtualFile(event.getOldParent().getPath()+"/"+event.getFileName()),
            event.getFile()
        );
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        if(event.getFileName().equals("project.dic")) {
            dictionary.registerAndNotify(event.getFile());
        }
    }
}
